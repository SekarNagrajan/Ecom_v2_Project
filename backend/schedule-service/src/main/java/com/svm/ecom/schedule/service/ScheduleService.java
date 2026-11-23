package com.svm.ecom.schedule.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.svm.ecom.common.util.PortLocalTimeUtil;
import com.svm.ecom.schedule.util.ScheduleOffsetDateTimeDeserializer;
import com.svm.ecom.schedule.config.ScheduleApiProperties;
import com.svm.ecom.schedule.model.Cutoff;
import com.svm.ecom.schedule.model.PortPair;
import com.svm.ecom.schedule.model.ScheduleEntry;
import com.svm.ecom.schedule.model.ScheduleResponse;
import com.svm.ecom.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;

import javax.annotation.PostConstruct;

@Service
public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ScheduleApiProperties apiProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    private ScheduleTimezoneCacheService timezoneCache;

    private RestTemplate restTemplate;

    /** ObjectMapper with lenient OffsetDateTime deserializer for external API date formats */
    private ObjectMapper scheduleParserMapper;

    @PostConstruct
    public void init() {
        this.restTemplate = createRestTemplate();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OffsetDateTime.class, new ScheduleOffsetDateTimeDeserializer());
        this.scheduleParserMapper = objectMapper.copy().registerModule(module);
    }

    /**
     * Validate JWT token using the centralized JwtUtil with enhanced security
     * Now includes session management to prevent token sharing/theft
     */
    public String validateTokenAndExtractUsername(String token) {
        try {
            // Use the most secure validation method that includes session checking
            String username = jwtUtil.validateTokenWithSession(token);
            logger.debug("Token validation successful for user: {}", username);
            return username;
        } catch (RuntimeException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            // Pass through the specific error message (e.g., "Token expired", "Token not found in active sessions")
            throw new SecurityException(e.getMessage());
        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            throw new SecurityException("JWT validation failed: " + e.getMessage());
        }
    }

    /**
     * Fetch schedule from external API with proper error handling and retries
     */
    public ScheduleResponse fetchSchedule(String origin, String delivery, String fromdate, String todate, String username) {
        String url = buildScheduleUrl(origin, delivery, fromdate, todate);
        logger.info("Fetching schedule from external API: {} for user: {} (origin: {}, delivery: {}, fromdate: {}, todate: {})", 
                   url, username, origin, delivery, fromdate, todate);

        for (int attempt = 1; attempt <= apiProperties.getMaxRetries(); attempt++) {
            try {
                logger.debug("Attempt {} of {} to fetch schedule", attempt, apiProperties.getMaxRetries());
                
                ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
                
                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("Successfully fetched schedule data for user: {}", username);
                    return parseExternalApiResponse(response.getBody(), username);
                } else {
                    logger.warn("External API returned non-200 status: {}", response.getStatusCode());
                    return new ScheduleResponse(null, String.valueOf(response.getStatusCodeValue()), 
                                              "External API error", username);
                }

            } catch (HttpClientErrorException e) {
                logger.error("Client error on attempt {}: {}", attempt, e.getMessage());
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return new ScheduleResponse(null, "404", "Schedule not found for given parameters", username);
                } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return new ScheduleResponse(null, "400", "Invalid request parameters", username);
                }
                // Don't retry on client errors
                break;
                
            } catch (HttpServerErrorException e) {
                logger.error("Server error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new ScheduleResponse(null, "500", "External API server error", username);
                }
                // Retry on server errors
                waitBeforeRetry();
                
            } catch (ResourceAccessException e) {
                logger.error("Connection error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new ScheduleResponse(null, "503", "External API unavailable", username);
                }
                // Retry on connection errors
                waitBeforeRetry();
                
            } catch (Exception e) {
                logger.error("Unexpected error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new ScheduleResponse(null, "500", "Internal server error", username);
                }
                waitBeforeRetry();
            }
        }

        return new ScheduleResponse(null, "500", "Failed to fetch schedule after " + apiProperties.getMaxRetries() + " attempts", username);
    }

    /**
     * Build the external API URL from request parameters
     */
    private String buildScheduleUrl(String origin, String delivery, String fromdate, String todate) {
        return apiProperties.getBaseUrl() + "/betweenlocation" +
                "?origin=" + origin +
                "&delivery=" + delivery +
                "&fromdate=" + fromdate +
                "&todate=" + todate;
    }

    /**
     * Create configured RestTemplate with timeouts
     */
    private RestTemplate createRestTemplate() {
        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        ((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout(apiProperties.getConnectionTimeout());
        ((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout(apiProperties.getReadTimeout());
        
        return new RestTemplate(factory);
    }

    /**
     * Parse the external API response and extract schedule data
     */
    private ScheduleResponse parseExternalApiResponse(Object responseBody, String username) {
        try {
            // Convert the response to JSON tree for parsing
            JsonNode rootNode = objectMapper.valueToTree(responseBody);
            
            // Extract the scheduleData array (root may be array or object with scheduleData/data/schedules)
            List<ScheduleEntry> scheduleData = null;
            JsonNode arrayNode = null;
            if (rootNode.isArray()) {
                arrayNode = rootNode;
            } else if (rootNode.isObject()) {
                arrayNode = rootNode.get("scheduleData");
                if (arrayNode == null) arrayNode = rootNode.get("data");
                if (arrayNode == null) arrayNode = rootNode.get("schedules");
            }
            if (arrayNode != null && arrayNode.isArray()) {
                scheduleData = scheduleParserMapper.convertValue(arrayNode,
                    new TypeReference<List<ScheduleEntry>>() {});
                logger.info("Successfully parsed {} schedule entries", scheduleData != null ? scheduleData.size() : 0);

                // Filter out inland legs, convert etd/eta and cutoff to port local time with timezone
                if (scheduleData != null) {
                    for (ScheduleEntry entry : scheduleData) {
                        filterInlandLegsAndUpdateEntry(entry);
                        convertEntryToPortLocalTimes(entry);
                    }
                }
            }

            // Extract other fields from the external API response
            String messageCode = getJsonStringValue(rootNode, "messageCode", "200");
            String message = getJsonStringValue(rootNode, "message", "Success");
            String timestamp = getJsonStringValue(rootNode, "timestamp", null);
            String processedBy = getJsonStringValue(rootNode, "processedBy", username);
            
            // Create and return the response
            ScheduleResponse response = new ScheduleResponse(scheduleData, messageCode, message, processedBy);
            if (timestamp != null) {
                response.setTimestamp(timestamp);
            }
            
            return response;
            
        } catch (Exception e) {
            logger.error("Failed to parse external API response: {}", e.getMessage(), e);
            return new ScheduleResponse(null, "500", "Failed to parse schedule data", username);
        }
    }
    
    /**
     * Filter out inland legs (portpairs where POL == POD after trimming) and update schedule entry fields
     * based on the filtered sea voyage legs
     */
    private void filterInlandLegsAndUpdateEntry(ScheduleEntry entry) {
        if (entry == null || entry.getPortpair() == null || entry.getPortpair().isEmpty()) {
            return;
        }
        
        // Filter portpairs - keep only those where POL != POD (actual sea voyages)
        List<PortPair> filteredPortpairs = new ArrayList<>();
        for (PortPair pp : entry.getPortpair()) {
            String pol = pp.getPol() != null ? pp.getPol().trim() : "";
            String pod = pp.getPod() != null ? pp.getPod().trim() : "";
            
            if (!pol.isEmpty() && !pod.isEmpty() && !pol.equalsIgnoreCase(pod)) {
                // Trim the POL and POD values
                pp.setPol(pol);
                pp.setPod(pod);
                filteredPortpairs.add(pp);
            } else {
                logger.debug("Filtering out inland leg: POL={}, POD={}", pp.getPol(), pp.getPod());
            }
        }
        
        // Update the portpair list with filtered results
        entry.setPortpair(filteredPortpairs);
        
        // Update entry-level fields based on filtered portpairs
        if (!filteredPortpairs.isEmpty()) {
            PortPair firstLeg = filteredPortpairs.get(0);
            PortPair lastLeg = filteredPortpairs.get(filteredPortpairs.size() - 1);
            
            // Update origin/delivery from first/last sea voyage legs
            entry.setOrigin(firstLeg.getPol());
            entry.setDelivery(lastLeg.getPod());
            entry.setOrigin_name(firstLeg.getPol_name());
            entry.setDelivery_name(lastLeg.getPod_name());
            
            // Update ETD/ETA from first/last sea voyage legs (strings; conversion to port local done in convertEntryToPortLocalTimes)
            entry.setEtd(firstLeg.getEtd());
            entry.setEta(lastLeg.getEta());

            // Update source/destination details
            entry.setSourcePortName(firstLeg.getPol_name());
            entry.setDestinationPortName(lastLeg.getPod_name());

            // Extract country from port name (format: "CITY, COUNTRY")
            entry.setSourceCountry(extractCountryFromPortName(firstLeg.getPol_name()));
            entry.setDestinationCountry(extractCountryFromPortName(lastLeg.getPod_name()));

            // Recalculate transit time from first/last leg ETD/ETA
            if (firstLeg.getEtd() != null && lastLeg.getEta() != null) {
                entry.setTransit_time((int) ChronoUnit.DAYS.between(firstLeg.getEtd().toInstant(), lastLeg.getEta().toInstant()));
            }
            
            // Set inland_flag to "N" since we filtered out inland legs
            entry.setInland_flag("N");
        }
    }
    
    /**
     * Convert ETD/ETA and cutoff in this entry to port local time with timezone offset.
     * PortPair ETD uses POL timezone, ETA uses POD timezone. Entry-level ETD/ETA and cutoff use origin port timezone.
     */
    private void convertEntryToPortLocalTimes(ScheduleEntry entry) {
        if (entry == null) return;
        if (timezoneCache == null) return;

        List<PortPair> portpairs = entry.getPortpair();
        if (portpairs != null) {
            for (PortPair pp : portpairs) {
                String polTz = timezoneCache.getTimezoneForPort(pp.getPol());
                String podTz = timezoneCache.getTimezoneForPort(pp.getPod());
                if (pp.getEtd() != null) {
                    pp.setEtd(PortLocalTimeUtil.labelWithOffset(pp.getEtd(), polTz));
                }
                if (pp.getEta() != null) {
                    pp.setEta(PortLocalTimeUtil.labelWithOffset(pp.getEta(), podTz));
                }
            }
            if (!portpairs.isEmpty()) {
                entry.setEtd(portpairs.get(0).getEtd());
                entry.setEta(portpairs.get(portpairs.size() - 1).getEta());
            }
        }

        String originTz = timezoneCache.getTimezoneForPort(entry.getOrigin());
        Cutoff cutoff = entry.getCutoff();
        if (cutoff != null && originTz != null) {
            if (cutoff.getDocClosing() != null) {
                cutoff.setDocClosing(PortLocalTimeUtil.labelWithOffset(cutoff.getDocClosing(), originTz));
            }
            if (cutoff.getVgmClosing() != null) {
                cutoff.setVgmClosing(PortLocalTimeUtil.labelWithOffset(cutoff.getVgmClosing(), originTz));
            }
            if (cutoff.getContClosing() != null) {
                cutoff.setContClosing(PortLocalTimeUtil.labelWithOffset(cutoff.getContClosing(), originTz));
            }
        }
    }

    /**
     * Extract country name from port name (format: "CITY, COUNTRY")
     */
    private String extractCountryFromPortName(String portName) {
        if (portName == null || portName.isEmpty()) {
            return null;
        }
        int commaIndex = portName.lastIndexOf(',');
        if (commaIndex >= 0 && commaIndex < portName.length() - 1) {
            return portName.substring(commaIndex + 1).trim();
        }
        return null;
    }
    
    /**
     * Helper method to safely extract string values from JSON node
     */
    private String getJsonStringValue(JsonNode rootNode, String fieldName, String defaultValue) {
        JsonNode fieldNode = rootNode.get(fieldName);
        if (fieldNode != null && !fieldNode.isNull()) {
            return fieldNode.asText();
        }
        return defaultValue;
    }

    /**
     * Wait before retry attempt
     */
    private void waitBeforeRetry() {
        try {
            Thread.sleep(apiProperties.getRetryDelay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Retry wait interrupted");
        }
    }
} 
