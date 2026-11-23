package com.svm.ecom.schedule.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.schedule.config.ScheduleApiProperties;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.schedule.enums.TransportMode;
import com.svm.ecom.schedule.model.CutoffVo;
import com.svm.ecom.schedule.model.PortScheduleResponse;
import com.svm.ecom.schedule.model.PortScheduleItem;
import com.svm.ecom.schedule.repository.CutoffConfigRepository;
import com.svm.ecom.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.time.format.DateTimeParseException;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import com.svm.ecom.common.util.PortLocalTimeUtil;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class PortService {

    private static final Logger logger = LoggerFactory.getLogger(PortService.class);

    @Autowired
    private ScheduleApiProperties apiProperties;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @Qualifier("reportEntityManagerFactory")
    private EntityManagerFactory reportEntityManagerFactory;

    @Autowired
    private ScheduleTimezoneCacheService timezoneCache;

    @Autowired
    private CutoffConfigRepository cutoffConfigRepository;

    @Autowired
    private CutoffCalculator cutoffCalculator;

    @Autowired
    private ObjectMapper objectMapper;
    
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        ((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout(apiProperties.getConnectionTimeout());
        ((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout(apiProperties.getReadTimeout());
        this.restTemplate = new RestTemplate(factory);
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

    public PortScheduleResponse fetchPortScheduleFromExternalService(String portCode, String fromDate, String todate, String username) {
        String url = buildUrl(portCode, fromDate, todate);
        logger.info("Fetching port schedule from URL: {}", url);
        logger.info("Search parameters - Port: {}, From: {}, To: {}, User: {}", portCode, fromDate, todate, username);

        for (int attempt = 1; attempt <= apiProperties.getMaxRetries(); attempt++) {
            try {
                logger.debug("Attempt {} of {} to fetch port schedule", attempt, apiProperties.getMaxRetries());
                
                // Create headers to ensure consistent API behavior
                HttpHeaders headers = new HttpHeaders();
                headers.set("User-Agent", "ECOMAPI-Service/1.0");
                headers.set("Accept", "application/json");
                headers.set("Content-Type", "application/json");
                // Add Origin header to match IP-based access
                headers.set("Origin", "http://localhost:8080");
                
                HttpEntity<?> requestEntity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

                logger.info("External API Response - Status: {}, Body length: {}", 
                           response.getStatusCode(), 
                           response.getBody() != null ? response.getBody().length() : 0);

                if (response.getStatusCode() == HttpStatus.OK) {
                    logger.debug("Response body: {}", response.getBody());
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode dataNode = root.get("schedules");
                    
                    if (dataNode == null) {
                        logger.warn("No 'schedules' node found in API response");
                        return new PortScheduleResponse("204", "No schedules data in API response", null, username);
                    }
                    
                    if (dataNode.isArray() && dataNode.size() == 0) {
                        logger.info("No port schedules found for port {} from {} to {}", portCode, fromDate, todate);
                        return new PortScheduleResponse("204", "No schedules found for the given criteria", null, username);
                    }
                    
                    List<PortScheduleItem> scheduleList = objectMapper.convertValue(
                            dataNode,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, PortScheduleItem.class)
                    );
                    
                    logger.info("Successfully found {} port schedules for port {}", scheduleList.size(), portCode);
                    return new PortScheduleResponse("200", "Success", scheduleList, username);
                } else {
                    logger.warn("External API returned non-200 status: {} - {}", response.getStatusCode(), response.getStatusCode().getReasonPhrase());
                    String responseBody = response.getBody();
                    if (responseBody != null && !responseBody.isEmpty()) {
                        logger.warn("Error response body: {}", responseBody);
                    }
                    return new PortScheduleResponse(String.valueOf(response.getStatusCodeValue()), "API returned error: " + response.getStatusCode().getReasonPhrase(), null, username);
                }

            } catch (HttpClientErrorException e) {
                logger.error("Client error on attempt {}: {}", attempt, e.getMessage());
                return new PortScheduleResponse(String.valueOf(e.getRawStatusCode()), "Client error: " + e.getStatusText(), null, username);
            } catch (HttpServerErrorException | ResourceAccessException e) {
                logger.error("Server or connection error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new PortScheduleResponse("503", "Service unavailable or timeout", null, username);
                }
                waitBeforeRetry();
            } catch (Exception e) {
                logger.error("Unexpected error on attempt {}: {}", attempt, e.getMessage(), e);
                if (attempt == apiProperties.getMaxRetries()) {
                    return new PortScheduleResponse("500", "Internal server error", null, username);
                }
                waitBeforeRetry();
            }
        }

        return new PortScheduleResponse("500", "Failed to fetch schedule after retries", null, username);
    }

    /**
     * Fetch port schedule using native SQL query from Report database
     */
    public PortScheduleResponse fetchPortScheduleFromDatabase(String portCode, String fromDate, String toDate, String username) {
        logger.info("Fetching port schedule from database for port: {}, from: {}, to: {}, user: {}", 
                   portCode, fromDate, toDate, username);
        
        EntityManager entityManager = null;
        try {
            // Create EntityManager from the existing factory
            entityManager = reportEntityManagerFactory.createEntityManager();
            
            String sql = "SELECT distinct uvs_ScheduleView.vessel ,vesselmaster.vesselname ,uvs_ScheduleView.voyage,uvs_ScheduleView.service," +
                        "uvs_ScheduleView.Bound, uvs_ScheduleView.port, Ports.portname,uvs_ScheduleView.Terminal, terminals.terminalname," +
                        "uvs_ScheduleView.arrdate,uvs_ScheduleView.depdate, uvs_ScheduleView.type,uvs_ScheduleView.status,uvs_ScheduleView.addhoc, " +
                        "uvs_ScheduleView.isOverlap from uvs_ScheduleView " +
                        "inner join vesselmaster on vesselmaster.vesselcode=uvs_ScheduleView.vessel " +
                        "inner join Ports on uvs_ScheduleView.port=Ports.portcode " +
                        "inner join terminals on uvs_ScheduleView.Terminal=terminals.TerminalCode " +
                        "Where uvs_scheduleview.port=? and uvs_scheduleview.arrdate between ? and ?";
            
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, portCode);
            query.setParameter(2, fromDate);
            query.setParameter(3, toDate);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            if (results == null || results.isEmpty()) {
                logger.info("No port schedules found for port {} from {} to {}", portCode, fromDate, toDate);
                return new PortScheduleResponse("204", "No schedules found for the given criteria", null, username);
            }
            
            String tzFromCache = timezoneCache != null ? timezoneCache.getTimezoneForPort(portCode) : null;
            String tzUsed = tzFromCache != null ? tzFromCache : resolveTimezoneForPort(portCode);
            logger.info("Port schedule timezone for port {}: getTimezoneForPort returned '{}', using '{}'",
                    portCode, tzFromCache != null ? tzFromCache : "null", tzUsed != null ? tzUsed : "null");

            List<PortScheduleItem> scheduleList = mapResultsToPortScheduleItems(results);

            // One DB round-trip: ecom_cutoffconfig for this port (+ All rows), then enrich each row
            if (!scheduleList.isEmpty()) {
                Map<String, Map<String, String>> cutoffConfigMap = cutoffConfigRepository.getCutoffConfigDetails(portCode);
                for (PortScheduleItem item : scheduleList) {
                    String arrDate = item.getArrivalDate() != null ? item.getArrivalDate().toString() : null;
                    Map<String, String> cutoffDates = cutoffCalculator.calculateCutoffDates(
                            arrDate, item.getPort(), item.getTerminal(), cutoffConfigMap);
                    applyCutoffToPortScheduleItem(item, cutoffDates);
                }
            }

            logger.info("Successfully found {} port schedules for port {} from database", scheduleList.size(), portCode);
            return new PortScheduleResponse("200", "Success", scheduleList, username);
            
        } catch (Exception e) {
            logger.error("Error fetching port schedule from database for port {}: {}", portCode, e.getMessage(), e);
            return new PortScheduleResponse("500", "Database error: " + e.getMessage(), null, username);
        } finally {
            // Always close the EntityManager to prevent resource leaks
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Map SQL query results to PortScheduleItem objects
     */
    private List<PortScheduleItem> mapResultsToPortScheduleItems(List<Object[]> results) {
        List<PortScheduleItem> scheduleList = new ArrayList<>();
        
        for (Object[] row : results) {
            try {
                PortScheduleItem item = new PortScheduleItem();
                
                // Map columns from SQL result to PortScheduleItem fields
                // SQL columns: vessel, vesselname, voyage, service, Bound, port, portname, Terminal, terminalname, arrdate, depdate, type, status, addhoc, isOverlap
                // PortScheduleItem fields: vessel, vesselName, voyage, service, bound, port, portName, terminal, terminalName, arrivalDate, departureDate, type
                
                item.setVessel(row[0] != null ? row[0].toString() : null);           // vessel
                item.setVesselName(row[1] != null ? row[1].toString() : null);       // vesselname
                item.setVoyage(row[2] != null ? row[2].toString() : null);           // voyage
                item.setService(row[3] != null ? row[3].toString() : null);          // service
                item.setBound(safeBound(row[4] != null ? row[4].toString() : null)); // Bound (enum)
                item.setPort(row[5] != null ? row[5].toString() : null);             // port
                item.setPortName(row[6] != null ? row[6].toString() : null);         // portname
                item.setTerminal(row[7] != null ? row[7].toString() : null);         // Terminal
                item.setTerminalName(row[8] != null ? row[8].toString() : null);     // terminalname
                item.setArrivalDate(parseToOffsetDateTime(row[9]));
                item.setDepartureDate(parseToOffsetDateTime(row[10]));
                String portCode = item.getPort();
                String tz = timezoneCache != null ? timezoneCache.getTimezoneForPort(portCode) : null;
                if (tz == null) tz = resolveTimezoneForPort(portCode);
                if (item.getArrivalDate() != null) {
                    item.setArrivalDate(PortLocalTimeUtil.labelWithOffset(item.getArrivalDate(), tz));
                }
                if (item.getDepartureDate() != null) {
                    item.setDepartureDate(PortLocalTimeUtil.labelWithOffset(item.getDepartureDate(), tz));
                }
                item.setType(safeTransportMode(row[11] != null ? row[11].toString() : null)); // type (enum)
                // Note: Skipping columns 12 (status), 13 (addhoc), 14 (isOverlap) as they're not in PortScheduleItem
                
                scheduleList.add(item);
                
            } catch (Exception e) {
                logger.warn("Error mapping row to PortScheduleItem: {}", e.getMessage());
                // Continue processing other rows
            }
        }
        
        return scheduleList;
    }

    private String buildUrl(String portCode, String fromDate, String todate) {
        String baseUrl = apiProperties.getBaseUrl();
        String fullUrl = baseUrl + "/byport?port=" + portCode + "&fromdate=" + fromDate + "&todate=" + todate;
        logger.info("API Configuration - Base URL: {}", baseUrl);
        logger.info("Full constructed URL: {}", fullUrl);
        return fullUrl;
    }

    /** Fallback when timezone cache has no entry (e.g. DB table missing). Ensures arrdate/depdate get an offset. */
    private static final Map<String, String> PORT_TIMEZONE_FALLBACK;
    static {
        Map<String, String> m = new HashMap<>();
        m.put("CNNGB", "+08:00");
        m.put("CNSHA", "+08:00");
        m.put("CNYTN", "+08:00");
        m.put("CNXMN", "+08:00");
        m.put("HKHKG", "+08:00");
        m.put("SGSIN", "+08:00");
        m.put("NLRTM", "+01:00");
        m.put("ESBCN", "+01:00");
        m.put("INMUN", "+05:30");
        PORT_TIMEZONE_FALLBACK = Collections.unmodifiableMap(m);
    }

    private static String resolveTimezoneForPort(String portCode) {
        if (portCode == null) return null;
        return PORT_TIMEZONE_FALLBACK.get(portCode.trim().toUpperCase());
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(apiProperties.getRetryDelay());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Retry wait interrupted");
        }
    }

    // ===== Safe enum conversion helpers =====

    /**
     * Safely convert String to Bound enum
     */
    private Bound safeBound(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Bound.fromValue(value.trim());
        } catch (Exception e) {
            logger.warn("Invalid Bound value: {}", value);
            return null;
        }
    }

    /**
     * Safely convert String to TransportMode enum
     */
    private TransportMode safeTransportMode(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return TransportMode.fromValue(value.trim());
        } catch (Exception e) {
            logger.warn("Invalid TransportMode value: {}", value);
            return null;
        }
    }

    private OffsetDateTime parseToOffsetDateTime(Object value) {
        if (value == null) return null;
        try {
            LocalDateTime ldt;
            if (value instanceof Timestamp) {
                ldt = ((Timestamp) value).toLocalDateTime();
            } else if (value instanceof LocalDateTime) {
                ldt = (LocalDateTime) value;
            } else {
                String strValue = value.toString().trim();
                if (strValue.isEmpty()) return null;
                if (strValue.contains(".")) {
                    strValue = strValue.substring(0, strValue.indexOf('.'));
                }
                ldt = LocalDateTime.parse(strValue, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            return ldt.atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            logger.warn("Error parsing date value: {}", value);
            return null;
        }
    }

    /** Maps calculator output to CutoffVo; sets cutoff to null when no usable times were produced. */
    private void applyCutoffToPortScheduleItem(PortScheduleItem item, Map<String, String> cutoffDates) {
        if (cutoffDates == null || cutoffDates.isEmpty()) {
            item.setCutoff(null);
            return;
        }
        CutoffVo vo = new CutoffVo();
        vo.setDocClosing(parseOffsetDateTimeOrNull(cutoffDates.get("docClosing")));
        vo.setVgmClosing(parseOffsetDateTimeOrNull(cutoffDates.get("vgmClosing")));
        vo.setContClosing(parseOffsetDateTimeOrNull(cutoffDates.get("contClosing")));
        if (vo.getDocClosing() == null && vo.getVgmClosing() == null && vo.getContClosing() == null) {
            item.setCutoff(null);
        } else {
            item.setCutoff(vo);
        }
    }

    private static OffsetDateTime parseOffsetDateTimeOrNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
