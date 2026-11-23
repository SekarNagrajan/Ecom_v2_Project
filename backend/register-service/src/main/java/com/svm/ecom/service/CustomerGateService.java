package com.svm.ecom.service;

import com.svm.ecom.dto.CustomerGateDto;
import com.svm.ecom.enums.GateStatus;
import com.svm.ecom.exception.CustomerGateRateLimitException;
import com.svm.ecom.repo.CustomerGateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class CustomerGateService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerGateService.class);

    private static final long WINDOW_MS = 60_000L;

    private final CustomerGateRepository repository;
    private final int rateLimitPerMinute;


    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public CustomerGateService(
            CustomerGateRepository repository,
            @Value("${ecom-customer-gate.rate-limit-per-minute:100}") int rateLimitPerMinute) {
        this.repository = repository;
        this.rateLimitPerMinute = Math.max(1, rateLimitPerMinute);
    }

    
    public List<CustomerGateDto> list(String clientId, String gateStatusParam, String updatedSinceParam) {
        checkRateLimit(clientId);

        String gateStatus = parseGateStatus(gateStatusParam);
        Timestamp updatedSince = parseUpdatedSince(updatedSinceParam);

        return repository.findAll(gateStatus, updatedSince);
    }

    private String parseGateStatus(String gateStatusParam) {
        if (gateStatusParam == null || gateStatusParam.trim().isEmpty()) {
            return null;
        }
        // Validate against the single source of truth; reject invalid letters as 400 rather
        // than silently matching zero rows.
        GateStatus parsed = GateStatus.fromValue(gateStatusParam.trim());
        return parsed == null ? null : parsed.getCode();
    }

    private Timestamp parseUpdatedSince(String updatedSinceParam) {
        if (updatedSinceParam == null || updatedSinceParam.trim().isEmpty()) {
            return null;
        }
        OffsetDateTime odt = OffsetDateTime.parse(updatedSinceParam.trim());
        return Timestamp.from(odt.toInstant());
    }

    private void checkRateLimit(String clientId) {
        String key = (clientId == null || clientId.trim().isEmpty()) ? "unknown" : clientId.trim();
        Window w = windows.computeIfAbsent(key, k -> new Window());

        long now = System.currentTimeMillis();
        long start = w.windowStartMs.get();
        if (now - start >= WINDOW_MS) {
            if (w.windowStartMs.compareAndSet(start, now)) {
                w.count.set(0);
            }
        }
        int current = w.count.incrementAndGet();
        if (current > rateLimitPerMinute) {
            long elapsed = now - w.windowStartMs.get();
            long retryAfterSeconds = Math.max(1L, (WINDOW_MS - elapsed) / 1000L);
            logger.warn("ecomCustomerGate rate limit exceeded for clientId={} ({}/{} per minute)",
                key, current, rateLimitPerMinute);
            throw new CustomerGateRateLimitException(
                "Rate limit exceeded: " + rateLimitPerMinute + " requests/min per Client ID", retryAfterSeconds);
        }
    }

    private static final class Window {
        final AtomicInteger count = new AtomicInteger(0);
        final AtomicLong windowStartMs = new AtomicLong(System.currentTimeMillis());
    }
}
