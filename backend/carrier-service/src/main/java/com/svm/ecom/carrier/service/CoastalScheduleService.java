package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.carrier.config.CarrierApiProperties;
import com.svm.ecom.carrier.model.CoastalScheduleBlock;
import com.svm.ecom.carrier.model.CoastalScheduleCall;
import com.svm.ecom.carrier.model.CoastalScheduleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoastalScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(CoastalScheduleService.class);
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String SCHEDULE_PATH = "/vdsmgr/api/svm/getscheduledetail";

    private final CarrierApiProperties apiProperties;
    private final ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Autowired
    public CoastalScheduleService(CarrierApiProperties apiProperties, ObjectMapper objectMapper) {
        this.apiProperties = apiProperties;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        this.restTemplate = createRestTemplate();
    }

    void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CoastalScheduleBlock> fetch(List<CoastalScheduleRequest> requests, String username) {
        if (requests == null || requests.isEmpty()) {
            throw new CoastalScheduleException(HttpStatus.BAD_REQUEST, "At least one schedule request is required");
        }
        String coastalToken = configuredToken();
        List<CoastalScheduleBlock> blocks = new ArrayList<CoastalScheduleBlock>();
        for (int i = 0; i < requests.size(); i++) {
            CoastalScheduleRequest request = requests.get(i);
            validate(request);
            blocks.add(new CoastalScheduleBlock(callLive(request, i, username, coastalToken)));
        }
        return blocks;
    }

    private void validate(CoastalScheduleRequest request) {
        if (request == null) {
            throw new CoastalScheduleException(HttpStatus.BAD_REQUEST, "schedule request is required");
        }
        requireText(request.getService(), "service");
        requireText(request.getVessel(), "vessel");
        LocalDate fromDate = requireDate(request.getFromDate(), "fromDate");
        LocalDate toDate = requireDate(request.getToDate(), "toDate");
        if (fromDate.isAfter(toDate)) {
            throw new CoastalScheduleException(HttpStatus.BAD_REQUEST, "fromDate must be on or before toDate");
        }
    }

    private List<CoastalScheduleCall> callLive(CoastalScheduleRequest request, int index, String username, String liveToken) {
        String url = buildUrl();
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put("service", request.getService().trim());
        values.put("vessel", request.getVessel().trim());
        values.put("fromdate", request.getFromDate().format(ISO_DATE));
        values.put("todate", request.getToDate().format(ISO_DATE));
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("values", values);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", liveToken);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(body, headers);

        logger.info("Fetching coastal schedule for user {} item {} service {} vessel {} from {} to {}",
                username, index, values.get("service"), values.get("vessel"), values.get("fromdate"), values.get("todate"));

        int attempts = Math.max(apiProperties.getMaxRetries(), 1);
        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return readCalls(response.getBody());
                }
                throw upstreamStatus(response.getStatusCode());
            } catch (CoastalScheduleException ex) {
                throw ex;
            } catch (HttpClientErrorException ex) {
                logger.error("Coastal schedule client error on attempt {}: {}", attempt, ex.getStatusCode());
                throw upstreamStatus(ex.getStatusCode());
            } catch (HttpServerErrorException ex) {
                logger.error("Coastal schedule server error on attempt {}: {}", attempt, ex.getStatusCode());
                if (attempt == attempts) {
                    throw upstreamStatus(ex.getStatusCode());
                }
                waitBeforeRetry();
            } catch (ResourceAccessException ex) {
                logger.error("Coastal schedule connection error on attempt {}: {}", attempt, ex.getMessage());
                if (attempt == attempts) {
                    throw new CoastalScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Coastal schedule service is unavailable");
                }
                waitBeforeRetry();
            } catch (Exception ex) {
                logger.error("Coastal schedule call failed on attempt {}: {}", attempt, ex.getMessage());
                if (attempt == attempts) {
                    throw new CoastalScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
                }
                waitBeforeRetry();
            }
        }
        throw new CoastalScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private List<CoastalScheduleCall> readCalls(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode funResponse = root.get("FUNRESPONSE");
            if (funResponse == null || funResponse.isNull()) {
                logger.error("Coastal schedule response did not include FUNRESPONSE");
                throw new CoastalScheduleException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Coastal schedule response did not include FUNRESPONSE");
            }
            if (!funResponse.isArray()) {
                throw new CoastalScheduleException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Coastal schedule response FUNRESPONSE was not a list");
            }
            List<CoastalScheduleCall> calls = CoastalScheduleMapper.mapCalls(funResponse, objectMapper);
            logger.info("Mapped {} coastal port calls", calls.size());
            return calls;
        } catch (CoastalScheduleException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Failed to parse coastal schedule response: {}", ex.getMessage());
            throw new CoastalScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse coastal schedule data");
        }
    }

    private String configuredToken() {
        String token = apiProperties.getCoastalToken();
        if (token == null || token.trim().isEmpty()) {
            throw new CoastalScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Coastal schedule API token is not configured");
        }
        return token.trim();
    }

    private String buildUrl() {
        String base = apiProperties.getCoastalBaseUrl();
        if (base == null || base.trim().isEmpty()) {
            throw new CoastalScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Coastal schedule API host is not configured");
        }
        String trimmed = base.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + SCHEDULE_PATH;
    }

    private CoastalScheduleException upstreamStatus(HttpStatus status) {
        if (status == HttpStatus.BAD_REQUEST) {
            return new CoastalScheduleException(status, "Invalid request parameters");
        }
        if (status == HttpStatus.FORBIDDEN) {
            return new CoastalScheduleException(status, "Coastal schedule request is forbidden");
        }
        if (status == HttpStatus.NOT_FOUND) {
            return new CoastalScheduleException(status, "Coastal schedule service was not found");
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return new CoastalScheduleException(status, "Coastal schedule service is unavailable");
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return new CoastalScheduleException(status, "Count Exceeded");
        }
        return new CoastalScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Coastal schedule service error");
    }

    private void requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new CoastalScheduleException(HttpStatus.BAD_REQUEST, field + " is required");
        }
    }

    private LocalDate requireDate(LocalDate value, String field) {
        if (value == null) {
            throw new CoastalScheduleException(HttpStatus.BAD_REQUEST, field + " is required");
        }
        return value;
    }

    private RestTemplate createRestTemplate() {
        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        ((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout(apiProperties.getConnectionTimeout());
        ((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout(apiProperties.getReadTimeout());
        return new RestTemplate(factory);
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(apiProperties.getRetryDelay());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.warn("Coastal schedule retry wait interrupted");
        }
    }
}
