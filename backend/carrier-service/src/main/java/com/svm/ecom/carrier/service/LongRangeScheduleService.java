package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.carrier.config.CarrierApiProperties;
import com.svm.ecom.carrier.model.LongRangeScheduleBlock;
import com.svm.ecom.carrier.model.LongRangeScheduleCall;
import com.svm.ecom.carrier.model.LongRangeScheduleRequest;
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
import java.util.Locale;
import java.util.Map;

@Service
public class LongRangeScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(LongRangeScheduleService.class);
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String SCHEDULE_PATH = "/vdsmgr/api/svm/getlongrangescheduleinfo";

    private final CarrierApiProperties apiProperties;
    private final ObjectMapper objectMapper;

    private RestTemplate restTemplate;

    @Autowired
    public LongRangeScheduleService(CarrierApiProperties apiProperties, ObjectMapper objectMapper) {
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

    public List<LongRangeScheduleBlock> fetch(List<LongRangeScheduleRequest> requests, String username) {
        if (requests == null || requests.isEmpty()) {
            throw new LongRangeScheduleException(HttpStatus.BAD_REQUEST, "At least one schedule request is required");
        }
        String coastalToken = configuredToken();
        List<LongRangeScheduleBlock> blocks = new ArrayList<LongRangeScheduleBlock>();
        for (int i = 0; i < requests.size(); i++) {
            LongRangeScheduleRequest request = requests.get(i);
            String weekStartDay = validate(request);
            blocks.add(new LongRangeScheduleBlock(callLive(request, weekStartDay, i, username, coastalToken)));
        }
        return blocks;
    }

    private String validate(LongRangeScheduleRequest request) {
        if (request == null) {
            throw new LongRangeScheduleException(HttpStatus.BAD_REQUEST, "schedule request is required");
        }
        requireText(request.getService(), "service");
        LocalDate fromDate = requireDate(request.getFromDate(), "fromDate");
        LocalDate toDate = requireDate(request.getToDate(), "toDate");
        if (fromDate.isAfter(toDate)) {
            throw new LongRangeScheduleException(HttpStatus.BAD_REQUEST, "fromDate must be on or before toDate");
        }
        return weekStartDay(request.getWeekStartDay());
    }

    private String weekStartDay(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "M";
        }
        String day = value.trim().toUpperCase(Locale.ENGLISH);
        if (!"M".equals(day) && !"S".equals(day)) {
            throw new LongRangeScheduleException(HttpStatus.BAD_REQUEST, "weekStartDay must be M or S");
        }
        return day;
    }

    private List<LongRangeScheduleCall> callLive(LongRangeScheduleRequest request, String weekStartDay, int index, String username, String liveToken) {
        String url = buildUrl();
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        values.put("service", request.getService().trim());
        values.put("fromDate", request.getFromDate().format(ISO_DATE));
        values.put("toDate", request.getToDate().format(ISO_DATE));
        values.put("weekStartDay", weekStartDay);
        Map<String, Object> body = new LinkedHashMap<String, Object>();
        body.put("values", values);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", liveToken);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(body, headers);

        logger.info("Fetching long range schedule for user {} item {} service {} from {} to {} weekStart {}",
                username, index, values.get("service"), values.get("fromDate"), values.get("toDate"), weekStartDay);

        int attempts = Math.max(apiProperties.getMaxRetries(), 1);
        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    return readCalls(response.getBody());
                }
                throw upstreamStatus(response.getStatusCode());
            } catch (LongRangeScheduleException ex) {
                throw ex;
            } catch (HttpClientErrorException ex) {
                logger.error("Long range schedule client error on attempt {}: {}", attempt, ex.getStatusCode());
                throw upstreamStatus(ex.getStatusCode());
            } catch (HttpServerErrorException ex) {
                logger.error("Long range schedule server error on attempt {}: {}", attempt, ex.getStatusCode());
                if (attempt == attempts) {
                    throw upstreamStatus(ex.getStatusCode());
                }
                waitBeforeRetry();
            } catch (ResourceAccessException ex) {
                logger.error("Long range schedule connection error on attempt {}: {}", attempt, ex.getMessage());
                if (attempt == attempts) {
                    throw new LongRangeScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Long range schedule service is unavailable");
                }
                waitBeforeRetry();
            } catch (Exception ex) {
                logger.error("Long range schedule call failed on attempt {}: {}", attempt, ex.getMessage());
                if (attempt == attempts) {
                    throw new LongRangeScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
                }
                waitBeforeRetry();
            }
        }
        throw new LongRangeScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private List<LongRangeScheduleCall> readCalls(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode funResponse = root.get("FUNRESPONSE");
            if (funResponse == null || funResponse.isNull()) {
                logger.error("Long range schedule response did not include FUNRESPONSE");
                throw new LongRangeScheduleException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Long range schedule response did not include FUNRESPONSE");
            }
            if (!funResponse.isArray()) {
                throw new LongRangeScheduleException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Long range schedule response FUNRESPONSE was not a list");
            }
            List<LongRangeScheduleCall> calls = new ArrayList<LongRangeScheduleCall>();
            for (JsonNode item : funResponse) {
                if (item != null && item.isObject()) {
                    calls.addAll(LongRangeScheduleMapper.mapCalls(item.get("longRangeSchedule"), objectMapper));
                }
            }
            logger.info("Mapped {} long range schedule rows", calls.size());
            return calls;
        } catch (LongRangeScheduleException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Failed to parse long range schedule response: {}", ex.getMessage());
            throw new LongRangeScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse long range schedule data");
        }
    }

    private String configuredToken() {
        String token = apiProperties.getLongRangeToken();
        if (token == null || token.trim().isEmpty()) {
            throw new LongRangeScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Long range schedule API token is not configured");
        }
        return token.trim();
    }

    private String buildUrl() {
        String base = apiProperties.getCoastalBaseUrl();
        if (base == null || base.trim().isEmpty()) {
            throw new LongRangeScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Long range schedule API host is not configured");
        }
        String trimmed = base.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed + SCHEDULE_PATH;
    }

    private LongRangeScheduleException upstreamStatus(HttpStatus status) {
        if (status == HttpStatus.BAD_REQUEST) {
            return new LongRangeScheduleException(status, "Invalid request parameters");
        }
        if (status == HttpStatus.FORBIDDEN) {
            return new LongRangeScheduleException(status, "Long range schedule request is forbidden");
        }
        if (status == HttpStatus.NOT_FOUND) {
            return new LongRangeScheduleException(status, "Long range schedule service was not found");
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return new LongRangeScheduleException(status, "Long range schedule service is unavailable");
        }
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            return new LongRangeScheduleException(status, "Count Exceeded");
        }
        return new LongRangeScheduleException(HttpStatus.INTERNAL_SERVER_ERROR, "Long range schedule service error");
    }

    private void requireText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new LongRangeScheduleException(HttpStatus.BAD_REQUEST, field + " is required");
        }
    }

    private LocalDate requireDate(LocalDate value, String field) {
        if (value == null) {
            throw new LongRangeScheduleException(HttpStatus.BAD_REQUEST, field + " is required");
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
            logger.warn("Long range schedule retry wait interrupted");
        }
    }
}
