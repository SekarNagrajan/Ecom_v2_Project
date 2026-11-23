package com.svm.ecom.schedule.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.schedule.config.ScheduleApiProperties;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.schedule.enums.TransportMode;
import com.svm.ecom.schedule.model.CutoffVo;
import com.svm.ecom.schedule.model.VesselScheduleResponse;
import com.svm.ecom.schedule.model.VesselScheduleItem;
import com.svm.ecom.schedule.repository.CutoffConfigRepository;
import com.svm.ecom.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.format.DateTimeParseException;
import java.time.OffsetDateTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.sql.Timestamp;

import com.svm.ecom.common.util.PortLocalTimeUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VesselService {

    private static final Logger logger = LoggerFactory.getLogger(VesselService.class);

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

    public VesselScheduleResponse fetchVesselScheduleFromExternalService(String vesselname, String fromDate, String todate, String username) {
        String url = buildUrl(vesselname, fromDate, todate);
        logger.info("Fetching vessel schedule from URL: {}", url);

        for (int attempt = 1; attempt <= apiProperties.getMaxRetries(); attempt++) {
            try {
                logger.debug("Attempt {} of {} to fetch vessel schedule", attempt, apiProperties.getMaxRetries());
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode dataNode = root.get("schedules");
                    List<VesselScheduleItem> scheduleList = objectMapper.convertValue(
                            dataNode,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, VesselScheduleItem.class)
                    );
                    return new VesselScheduleResponse("200", "Success", scheduleList, username);
                } else {
                    logger.warn("External API returned non-200 status: {}", response.getStatusCode());
                    return new VesselScheduleResponse(String.valueOf(response.getStatusCodeValue()), "API returned error", null, username);
                }

            } catch (HttpClientErrorException e) {
                logger.error("Client error on attempt {}: {}", attempt, e.getMessage());
                return new VesselScheduleResponse(String.valueOf(e.getRawStatusCode()), "Client error: " + e.getStatusText(), null, username);
            } catch (HttpServerErrorException | ResourceAccessException e) {
                logger.error("Server or connection error on attempt {}: {}", attempt, e.getMessage());
                if (attempt == apiProperties.getMaxRetries()) {
                    return new VesselScheduleResponse("503", "Service unavailable or timeout", null);
                }
                waitBeforeRetry();
            } catch (Exception e) {
                logger.error("Unexpected error on attempt {}: {}", attempt, e.getMessage(), e);
                if (attempt == apiProperties.getMaxRetries()) {
                    return new VesselScheduleResponse("500", "Internal server error", null);
                }
                waitBeforeRetry();
            }
        }

        return new VesselScheduleResponse("500", "Failed to fetch schedule after retries", null);
    }

    /**
     * Fetch vessel schedule using native SQL query from Report database
     */
    @Transactional(transactionManager = "reportTransactionManager", readOnly = true)
    public VesselScheduleResponse fetchVesselScheduleFromDatabase(String vessel, String fromDate, String toDate, String username) {
        logger.info("Fetching vessel schedule from database for vessel: {}, from: {}, to: {}, user: {}", 
                   vessel, fromDate, toDate, username);
        
        EntityManager entityManager = null;
        try {
            // Create EntityManager from the existing factory
            entityManager = reportEntityManagerFactory.createEntityManager();
            
            String sql = "SELECT distinct uvs_scheduleview.vessel,ports.portname,vesselmaster.vesselname ,uvs_scheduleview.voyage," +
                        "uvs_scheduleview.service, uvs_scheduleview.Bound,uvs_scheduleview.port,uvs_scheduleview.Terminal, terminals.terminalname," +
                        "uvs_scheduleview.arrdate, uvs_scheduleview.depdate,uvs_ScheduleView.type,uvs_scheduleview.status,uvs_scheduleview.addhoc," +
                        "uvs_scheduleview.isOverlap from uvs_ScheduleView uvs_scheduleview " +
                        "inner join vesselmaster on vesselmaster.vesselcode=uvs_scheduleview.vessel " +
                        "inner join Ports on uvs_scheduleview.port=Ports.portcode " +
                        "inner join terminals on uvs_scheduleview.Terminal=terminals.TerminalCode " +
                        "where uvs_scheduleview.status<>'D' and uvs_scheduleview.vessel=? and uvs_scheduleview.arrdate between ? and ?";
            
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, vessel);
            query.setParameter(2, fromDate);
            query.setParameter(3, toDate);
            
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            if (results == null || results.isEmpty()) {
                logger.info("No vessel schedules found for vessel {} from {} to {}", vessel, fromDate, toDate);
                return new VesselScheduleResponse("204", "No schedules found for the given criteria", null, username);
            }
            
            List<VesselScheduleItem> scheduleList = mapResultsToVesselScheduleItems(results);

            // One DB round-trip: all distinct ports in the result (+ All), then enrich each row
            if (!scheduleList.isEmpty()) {
                Set<String> distinctPorts = scheduleList.stream()
                        .map(VesselScheduleItem::getPort)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toSet());
                Map<String, Map<String, String>> cutoffConfigMap = distinctPorts.isEmpty()
                        ? Collections.emptyMap()
                        : cutoffConfigRepository.getCutoffConfigDetails(distinctPorts);
                for (VesselScheduleItem item : scheduleList) {
                    String arrDate = item.getArrivalDate() != null ? item.getArrivalDate().toString() : null;
                    Map<String, String> cutoffDates = cutoffCalculator.calculateCutoffDates(
                            arrDate, item.getPort(), item.getTerminal(), cutoffConfigMap);
                    applyCutoffToVesselScheduleItem(item, cutoffDates);
                }
            }

            logger.info("Successfully found {} vessel schedules for vessel {} from database", scheduleList.size(), vessel);
            return new VesselScheduleResponse("200", "Success", scheduleList, username);
            
        } catch (Exception e) {
            logger.error("Error fetching vessel schedule from database for vessel {}: {}", vessel, e.getMessage(), e);
            return new VesselScheduleResponse("500", "Database error: " + e.getMessage(), null, username);
        } finally {
            // Always close the EntityManager to prevent resource leaks
            if (entityManager != null && entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    /**
     * Map SQL query results to VesselScheduleItem objects
     */
    private List<VesselScheduleItem> mapResultsToVesselScheduleItems(List<Object[]> results) {
        List<VesselScheduleItem> scheduleList = new ArrayList<>();
        
        for (Object[] row : results) {
            try {
                VesselScheduleItem item = new VesselScheduleItem();
                
                // Map columns from SQL result to VesselScheduleItem fields
                // SQL columns: vessel, portname, vesselname, voyage, service, Bound, port, Terminal, terminalname, arrdate, depdate, type, status, addhoc, isOverlap
                // VesselScheduleItem fields: vessel, vesselName, voyage, service, bound, port, portName, terminal, terminalName, arrivalDate, departureDate, type
                
                item.setVessel(row[0] != null ? row[0].toString() : null);           // vessel
                item.setPortName(row[1] != null ? row[1].toString() : null);         // portname
                item.setVesselName(row[2] != null ? row[2].toString() : null);       // vesselname
                item.setVoyage(row[3] != null ? row[3].toString() : null);           // voyage
                item.setService(row[4] != null ? row[4].toString() : null);          // service
                item.setBound(safeBound(row[5] != null ? row[5].toString() : null)); // Bound (enum)
                item.setPort(row[6] != null ? row[6].toString() : null);             // port
                item.setTerminal(row[7] != null ? row[7].toString() : null);         // Terminal
                item.setTerminalName(row[8] != null ? row[8].toString() : null);     // terminalname
                item.setArrivalDate(parseToOffsetDateTime(row[9]));
                item.setDepartureDate(parseToOffsetDateTime(row[10]));
                String portCode = item.getPort();
                String tz = timezoneCache != null ? timezoneCache.getTimezoneForPort(portCode) : null;
                if (item.getArrivalDate() != null) {
                    item.setArrivalDate(PortLocalTimeUtil.labelWithOffset(item.getArrivalDate(), tz));
                }
                if (item.getDepartureDate() != null) {
                    item.setDepartureDate(PortLocalTimeUtil.labelWithOffset(item.getDepartureDate(), tz));
                }
                item.setType(safeTransportMode(row[11] != null ? row[11].toString() : null)); // type (enum)
                // Note: Skipping columns 12 (status), 13 (addhoc), 14 (isOverlap) as they're not in VesselScheduleItem
                
                scheduleList.add(item);
                
            } catch (Exception e) {
                logger.warn("Error mapping row to VesselScheduleItem: {}", e.getMessage());
                // Continue processing other rows
            }
        }
        
        return scheduleList;
    }

    private String buildUrl(String vesselname, String fromDate, String todate) {
        return apiProperties.getBaseUrl() + "/byvessel?vessel=" + vesselname + "&fromdate=" + fromDate + "&todate=" + todate;
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

    private void applyCutoffToVesselScheduleItem(VesselScheduleItem item, Map<String, String> cutoffDates) {
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
