package com.svm.ecom.master.service;

import com.svm.ecom.master.dto.v2.ImdgEntryResponse;
import com.svm.ecom.common.master.repo.ImdgMasterRepository;
import com.svm.ecom.common.master.repo.ImdgMasterRepository.ImdgRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for IMDG master v2 lookups (Documentation DB {@code subs}).
 */
@Service
public class ImdgMasterV2Service {

    private static final Logger logger = LoggerFactory.getLogger(ImdgMasterV2Service.class);

    @Autowired
    private ImdgMasterRepository imdgMasterRepository;

    public List<ImdgEntryResponse> findByUnNo(String unNo, String variant) {
        logger.debug("Fetching IMDG entries for unNo={}, variant={}", unNo, variant);
        List<ImdgRow> rows = imdgMasterRepository.findByUnNo(unNo, variant);
        if (rows == null || rows.isEmpty()) {
            logger.info("No IMDG entries for unNo={}", unNo);
            return Collections.emptyList();
        }

        String amendment = imdgMasterRepository.findCurrentAmendment();
        String segregation = imdgMasterRepository.findSegregationByUnNo(unNo);
        Map<String, String> stowCache = new HashMap<>();

        List<ImdgEntryResponse> result = rows.stream()
                .map(row -> toResponse(row, amendment, segregation, stowCache))
                .collect(Collectors.toList());
        logger.debug("Mapped {} IMDG rows", result.size());
        return result;
    }

    private ImdgEntryResponse toResponse(
            ImdgRow row, String amendment, String segregation, Map<String, String> stowCache) {
        ImdgEntryResponse r = new ImdgEntryResponse();
        r.setUnNo(row.getUnNo());
        r.setVariant(row.getVariant());
        r.setImdgAmendment(amendment);
        r.setImoClass(row.getImoClass());
        r.setSubRisk1(row.getSubRisk1());
        r.setSubRisk2(row.getSubRisk2());
        r.setPackagingGroup(normalizePackagingGroup(row.getPackagingGroup()));
        r.setEms(row.getEms());
        r.setState(normalizeState(row.getState()));
        r.setProperShippingName(row.getProperShippingName());
        r.setTechnicalName(row.getTechnicalName());
        r.setImoStow(row.getImoStow());
        r.setSegregation(segregation);

        String stowage = resolveStowage(row.getDglPhrase(), row.getStow(), stowCache);
        r.setStowageInstruction(stowage);
        return r;
    }

    private String resolveStowage(String dglPhrase, String stowFallback, Map<String, String> cache) {
        String key = dglPhrase == null ? "" : dglPhrase;
        if (cache.containsKey(key)) {
            String cached = cache.get(key);
            return cached != null ? cached : emptyToNull(stowFallback);
        }
        String resolved = imdgMasterRepository.resolveStowageInstruction(dglPhrase);
        cache.put(key, resolved);
        return resolved != null ? resolved : emptyToNull(stowFallback);
    }

    private static String normalizePackagingGroup(String pg) {
        String v = emptyToNull(pg);
        if (v == null) {
            return null;
        }
        String upper = v.toUpperCase(Locale.ROOT);
        if ("I".equals(upper) || "II".equals(upper) || "III".equals(upper)) {
            return upper;
        }
        return v;
    }

    private static String normalizeState(String state) {
        String v = emptyToNull(state);
        if (v == null) {
            return null;
        }
        return v.toUpperCase(Locale.ROOT);
    }

    private static String emptyToNull(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }
}
