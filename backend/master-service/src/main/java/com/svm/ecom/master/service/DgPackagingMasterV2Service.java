package com.svm.ecom.master.service;

import com.svm.ecom.master.dto.v2.DgPackagingResponse;
import com.svm.ecom.master.enums.DgPackagingType;
import com.svm.ecom.common.master.repo.DgPackagingMasterRepository;
import com.svm.ecom.common.master.repo.DgPackagingMasterRepository.DgPackagingRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service for DG packaging master v2.
 */
@Service
public class DgPackagingMasterV2Service {

    private static final Logger logger = LoggerFactory.getLogger(DgPackagingMasterV2Service.class);

    @Autowired
    private DgPackagingMasterRepository dgPackagingMasterRepository;

    /**
     * @param usage OUTER, INNER, or BOTH (optional); maps to exact DB type {@code o} / {@code i} / {@code b}
     * @param code  exact packaging code (optional)
     */
    public List<DgPackagingResponse> findAll(String usage, String code) {
        String usageDb = toUsageDbFilter(usage);
        logger.debug("Fetching DG packagings usage={}, code={}", usage, code);
        List<DgPackagingRow> rows = dgPackagingMasterRepository.findAll(usageDb, code);
        if (rows == null || rows.isEmpty()) {
            logger.info("No DG packaging rows found");
            return Collections.emptyList();
        }
        return rows.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private DgPackagingResponse toResponse(DgPackagingRow row) {
        DgPackagingResponse r = new DgPackagingResponse();
        r.setCode(row.getCode());
        r.setName(row.getName());
        try {
            r.setPackagingType(DgPackagingType.fromValue(row.getPackType()));
        } catch (IllegalArgumentException ex) {
            logger.warn("Unknown DG packaging type '{}' for code {}", row.getPackType(), row.getCode());
            r.setPackagingType(null);
        }
        return r;
    }

    /**
     * @return {@code o}, {@code i}, or {@code b} for exact usage filter, or null for all
     */
    private static String toUsageDbFilter(String usage) {
        if (usage == null || usage.trim().isEmpty()) {
            return null;
        }
        String u = usage.trim().toUpperCase(Locale.ROOT);
        if ("OUTER".equals(u) || "O".equals(u)) {
            return "o";
        }
        if ("INNER".equals(u) || "I".equals(u)) {
            return "i";
        }
        if ("BOTH".equals(u) || "B".equals(u)) {
            return "b";
        }
        throw new IllegalArgumentException(
                "Invalid usage value: " + usage + ". Valid values are: OUTER, INNER, BOTH");
    }
}
