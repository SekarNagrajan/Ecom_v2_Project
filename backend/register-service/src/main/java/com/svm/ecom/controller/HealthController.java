package com.svm.ecom.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Liveness/readiness probes (NFR for JIRA TSHG-3008: {@code /health/live}, {@code /health/ready}).
 *
 * <p>OPEN ITEM / infra gap: this codebase has NO existing health/actuator convention to reuse
 * ({@code register-service} has zero actuator wiring; a few sibling modules \u2014 booking-service,
 * summary-service, sibl-service \u2014 expose only {@code management.endpoints.web.exposure.include=
 * health,info} via spring-boot-starter-actuator, with no Prometheus registry anywhere in this
 * repo). Rather than pull in actuator + touch every other module's exposure config, this is a
 * minimal, additive, unauthenticated pair of endpoints scoped to this module only. Prometheus
 * metrics for this endpoint are NOT implemented \u2014 flagged as a separate open item, since no
 * Prometheus wiring exists anywhere in this codebase to plug into.</p>
 */
@RestController
public class HealthController {

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    private final JdbcTemplate reportJdbcTemplate;

    public HealthController(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /** Liveness: process is up and able to handle HTTP requests. No dependency checks. */
    @GetMapping("/health/live")
    public ResponseEntity<Map<String, Object>> live() {
        return ResponseEntity.ok(status("UP"));
    }

    /** Readiness: reportdb (read replica) is reachable. */
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        try {
            if (reportJdbcTemplate != null) {
                reportJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            }
            return ResponseEntity.ok(status("UP"));
        } catch (Exception e) {
            logger.warn("Readiness check failed: reportdb unreachable", e);
            Map<String, Object> body = status("DOWN");
            body.put("reason", "reportdb unreachable");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }

    /** Mutable on purpose: {@link #ready()} adds a "reason" key on the DOWN path. */
    private static Map<String, Object> status(String status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        return body;
    }
}
