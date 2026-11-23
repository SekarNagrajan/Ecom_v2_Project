package com.svm.ecom.tracking.service;

import com.svm.ecom.tracking.config.TrackingApiProperties;
import com.svm.ecom.tracking.model.TrackingRequest;
import com.svm.ecom.tracking.model.TrackingResponse;
import com.svm.ecom.tracking.model.TrackingData;
import com.svm.ecom.tracking.model.TrackingResult;
import com.svm.ecom.tracking.model.Movement;
import com.svm.ecom.tracking.model.ContainerActivity;
import com.svm.ecom.tracking.model.PortCall;
import com.svm.ecom.tracking.repository.TerminalRepository;
import com.svm.ecom.common.util.PortLocalTimeUtil;
import com.svm.ecom.security.JwtUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import javax.annotation.PostConstruct;

@Service
public class TrackingService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingService.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TrackingApiProperties apiProperties;

    @Autowired
    private TerminalRepository terminalRepository;

    @Autowired(required = false)
    private TrackingTimezoneCacheService timezoneCache;

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        this.restTemplate = createRestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Validate JWT token using the centralized JwtUtil
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
     * Fetch tracking data from external API with proper error handling and retries
     */
    public TrackingResponse fetchTracking(TrackingRequest request, String username) {
        String url = buildTrackingUrl(request);
        logger.info("Fetching tracking data from external API: {} for user: {} (key: {}, container: {})", url, username, request.getKey(), request.getStrcontainer());

        for (int attempt = 1; attempt <= apiProperties.getMaxRetries(); attempt++) {
            try {
                logger.debug("Attempt {} of {} to fetch tracking data", attempt, apiProperties.getMaxRetries());
                
                ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
                
                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.info("Successfully fetched tracking data for container: {} by user: {}", 
                               request.getStrcontainer(), username);
                    
                    // Deserialize the response body to TrackingData
                    TrackingData trackingData = null;
                    try {
                        trackingData = objectMapper.convertValue(response.getBody(), TrackingData.class);
                        
                        // Check if tracking data is effectively empty (no meaningful data)
                        if (trackingData != null && isTrackingDataEmpty(trackingData)) {
                            logger.info("External API returned empty tracking data for container: {}", request.getStrcontainer());
                            trackingData.setResult(null);
                        } else if (trackingData != null) {
                            // Populate terminal names and convert dates for all tracking results
                            populateTerminalNames(trackingData);
                            convertDatesToIsoFormat(trackingData);
                        }
                        
                    } catch (Exception e) {
                        logger.warn("Failed to deserialize tracking data: {}", e.getMessage());
                        // Return with null tracking data if deserialization fails
                    }
                    
                    return new TrackingResponse(trackingData, "200", "Success", username, request.getStrcontainer());
                } else {
                    logger.warn("External API returned non-200 status: {}", response.getStatusCode());
                    return new TrackingResponse(null, String.valueOf(response.getStatusCodeValue()), 
                                              "External API error", username, request.getStrcontainer());
                }

            } catch (HttpClientErrorException e) {
                logger.error("Client error on attempt {}: {}", attempt, e.getMessage());
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return new TrackingResponse(null, "404", "Container not found: " + request.getStrcontainer(), 
                                              username, request.getStrcontainer());
                } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return new TrackingResponse(null, "400", "Invalid container ID or request parameters", 
                                              username, request.getStrcontainer());
                }
                // Don't retry on client errors
                break;
                
            } catch (HttpServerErrorException e) {
                logger.error("Server error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new TrackingResponse(null, "500", "External tracking API server error", 
                                              username, request.getStrcontainer());
                }
                // Retry on server errors
                waitBeforeRetry();
                
            } catch (ResourceAccessException e) {
                logger.error("Connection error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new TrackingResponse(null, "503", "External tracking API unavailable", 
                                              username, request.getStrcontainer());
                }
                // Retry on connection errors
                waitBeforeRetry();
                
            } catch (Exception e) {
                logger.error("Unexpected error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new TrackingResponse(null, "500", "Internal server error", 
                                              username, request.getStrcontainer());
                }
                waitBeforeRetry();
            }
        }

        return new TrackingResponse(null, "500", 
                                  "Failed to fetch tracking data after " + apiProperties.getMaxRetries() + " attempts", 
                                  username, request.getStrcontainer());
    }

    /**
     * Build the external API URL from request parameters
     */
    private String buildTrackingUrl(TrackingRequest request) {
        StringBuilder url = new StringBuilder(apiProperties.getBaseUrl());
        url.append("/bytrackMovement");
        url.append("?key=").append(request.getKey());
        
        // Add strcontainer parameter only if provided
        if (request.getStrcontainer() != null && !request.getStrcontainer().trim().isEmpty()) {
            url.append("&strcontainer=").append(request.getStrcontainer());
        }
        
        return url.toString();
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

    /**
     * Check if tracking data is effectively empty (no meaningful data from external API).
     * External API returns empty results with empty loadarrdate and empty movementList
     * when no tracking data is found.
     */
    private boolean isTrackingDataEmpty(TrackingData trackingData) {
        if (trackingData == null || trackingData.getResult() == null || trackingData.getResult().isEmpty()) {
            return true;
        }

        // Check if all results are effectively empty (no meaningful data)
        for (TrackingResult result : trackingData.getResult()) {
            if (result == null) {
                continue;
            }
            // Check if result has any meaningful data
            // A result is considered "not empty" if it has any of these fields populated
            if (hasValue(result.getBlNo()) ||
                hasValue(result.getBookNo()) ||
                hasValue(result.getOriginPortDesc()) ||
                hasValue(result.getFinalPortDesc()) ||
                hasValue(result.getDischargePortDesc()) ||
                hasValue(result.getOriginPortCode()) ||
                hasValue(result.getDeliveryPortCode()) ||
                hasValue(result.getLoadportid()) ||
                hasValue(result.getPolterminal()) ||
                hasValue(result.getPodterminal()) ||
                result.getPolAt() != null ||
                result.getPodAt() != null ||
                result.getLoadarrdate() != null ||
                (result.getMovementList() != null && !result.getMovementList().isEmpty()) ||
                (result.getPortacklist() != null && !result.getPortacklist().isEmpty())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to check if a string value is non-null and non-empty
     */
    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Populate terminal names for all tracking results using the terminal code
     */
    private void populateTerminalNames(TrackingData trackingData) {
        if (trackingData == null || trackingData.getResult() == null) {
            return;
        }

        logger.debug("Populating terminal names for {} tracking results", trackingData.getResult().size());

        for (TrackingResult result : trackingData.getResult()) {
            try {
                // Populate POL terminal name
                String polTerminalCode = result.getPolterminal();
                if (polTerminalCode != null && !polTerminalCode.trim().isEmpty()) {
                    String terminalName = terminalRepository.getTerminalNameByCode(polTerminalCode);
                    result.setPolterminalname(terminalName);
                    
                    if (terminalName != null) {
                        logger.debug("Populated POL terminal name '{}' for code '{}'", terminalName, polTerminalCode);
                    } else {
                        logger.debug("No POL terminal name found for code '{}'", polTerminalCode);
                    }
                }

                // Populate POD terminal name
                String podTerminalCode = result.getPodterminal();
                if (podTerminalCode != null && !podTerminalCode.trim().isEmpty()) {
                    String terminalName = terminalRepository.getTerminalNameByCode(podTerminalCode);
                    result.setPodterminalname(terminalName);
                    
                    if (terminalName != null) {
                        logger.debug("Populated POD terminal name '{}' for code '{}'", terminalName, podTerminalCode);
                    } else {
                        logger.debug("No POD terminal name found for code '{}'", podTerminalCode);
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to populate terminal name for result with BL: {} - Error: {}", 
                           result.getBlNo(), e.getMessage());
            }
        }
    }

    /**
     * Convert all date fields to ISO 8601 with port local timezone offset.
     * Parses incoming dates (UTC or no offset), formats with port TZ using timezone cache.
     */
    private void convertDatesToIsoFormat(TrackingData trackingData) {
        if (trackingData == null || trackingData.getResult() == null) {
            return;
        }

        logger.debug("Converting date fields to port local ISO format for {} tracking results", trackingData.getResult().size());

        for (TrackingResult result : trackingData.getResult()) {
            try {
                // Convert UTC (from API) to port local OffsetDateTime
                if (result.getPolAt() != null && result.getLoadportid() != null) {
                    result.setPolAt(PortLocalTimeUtil.labelWithOffset(result.getPolAt(), timezoneCache != null ? timezoneCache.getTimezoneForPort(result.getLoadportid()) : null));
                }
                if (result.getPodAt() != null && result.getDeliveryPortCode() != null) {
                    result.setPodAt(PortLocalTimeUtil.labelWithOffset(result.getPodAt(), timezoneCache != null ? timezoneCache.getTimezoneForPort(result.getDeliveryPortCode()) : null));
                }
                if (result.getLoadarrdate() != null && result.getLoadportid() != null) {
                    result.setLoadarrdate(PortLocalTimeUtil.labelWithOffset(result.getLoadarrdate(), timezoneCache != null ? timezoneCache.getTimezoneForPort(result.getLoadportid()) : null));
                }

                // Convert dates in movementList -> containerActivity (activity at activityloccode)
                if (result.getMovementList() != null) {
                    for (Movement movement : result.getMovementList()) {
                        if (movement.getContainerActivity() != null) {
                            for (ContainerActivity activity : movement.getContainerActivity()) {
                                if (activity.getActivityDate() != null && activity.getActivityloccode() != null) {
                                    activity.setActivityDate(PortLocalTimeUtil.labelWithOffset(activity.getActivityDate(), timezoneCache != null ? timezoneCache.getTimezoneForPort(activity.getActivityloccode()) : null));
                                }
                            }
                        }
                    }
                }

                // Convert dates in portacklist: etd at loadportid, eta at dischargeportid
                if (result.getPortacklist() != null) {
                    for (PortCall portCall : result.getPortacklist()) {
                        if (portCall.getEtd() != null && portCall.getLoadportid() != null) {
                            portCall.setEtd(PortLocalTimeUtil.labelWithOffset(portCall.getEtd(), timezoneCache != null ? timezoneCache.getTimezoneForPort(portCall.getLoadportid()) : null));
                        }
                        if (portCall.getEta() != null && portCall.getDischargeportid() != null) {
                            portCall.setEta(PortLocalTimeUtil.labelWithOffset(portCall.getEta(), timezoneCache != null ? timezoneCache.getTimezoneForPort(portCall.getDischargeportid()) : null));
                        }
                    }
                }

                logger.debug("Successfully converted dates for BL: {}", result.getBlNo());
            } catch (Exception e) {
                logger.warn("Failed to convert dates for result with BL: {} - Error: {}",
                           result.getBlNo(), e.getMessage());
            }
        }
    }

}