package com.svm.ecom.master.service;

import com.svm.ecom.master.dto.v2.CommodityGroupResponse;
import com.svm.ecom.master.dto.v2.CommodityResponse;
import com.svm.ecom.master.enums.CommodityType;
import com.svm.ecom.master.repository.masters.CommodityMasterV2Repository;
import com.svm.ecom.master.repository.masters.CommodityRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Commodity Master v2 (Masters DB).
 */
@Service
public class CommodityMasterV2Service {

    private static final Logger logger = LoggerFactory.getLogger(CommodityMasterV2Service.class);

    @Autowired
    private CommodityMasterV2Repository commodityMasterV2Repository;

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager")
    public List<CommodityResponse> findAllCommodities() {
        logger.debug("Fetching all active commodities from Masters DB");
        List<CommodityRow> rows = commodityMasterV2Repository.findAllActiveCommodities();
        if (rows == null || rows.isEmpty()) {
            logger.info("No active commodities found in Masters DB");
            return Collections.emptyList();
        }
        List<CommodityResponse> result = rows.stream()
                .map(this::toCommodityResponse)
                .collect(Collectors.toList());
        logger.debug("Mapped {} commodity rows to response", result.size());
        return result;
    }

    private CommodityResponse toCommodityResponse(CommodityRow row) {
        CommodityResponse r = new CommodityResponse();
        r.setCode(emptyToNull(row.getComdtyCode()));
        r.setName(emptyToNull(row.getComdtyName()));
        r.setType(CommodityType.fromValue(row.getComdtyType()));
        r.setGroup(new CommodityGroupResponse(
                emptyToNull(row.getGrpCode()),
                emptyToNull(row.getGroupName())
        ));
        r.setRestricted(toBoolean(row.getRestricted()));
        r.setExempt(toBoolean(row.getExemptComdty()));
        return r;
    }

    /** Y/N -> true/false. Anything other than "Y" (case-insensitive) is false. */
    private static boolean toBoolean(String value) {
        return "Y".equalsIgnoreCase(value != null ? value.trim() : null);
    }

    private static String emptyToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }
}
