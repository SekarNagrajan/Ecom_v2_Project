package com.svm.ecom.master.service;

import com.svm.ecom.master.dto.v2.AddressResponse;
import com.svm.ecom.master.dto.v2.AgencyPortResponse;
import com.svm.ecom.master.dto.v2.AgencyResponse;
import com.svm.ecom.master.repository.masters.AgencyMasterV2Repository;
import com.svm.ecom.master.repository.masters.AgencyPortRow;
import com.svm.ecom.master.repository.masters.AgencyRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for Agency Master v2 (Masters DB).
 */
@Service
public class AgencyMasterV2Service {

    private static final Logger logger = LoggerFactory.getLogger(AgencyMasterV2Service.class);

    @Autowired
    private AgencyMasterV2Repository agencyMasterV2Repository;

    @Transactional(readOnly = true, transactionManager = "mastersTransactionManager")
    public List<AgencyResponse> findAllAgencies() {
        logger.debug("Fetching all active agencies from Masters DB");

        List<AgencyRow> agencyRows = agencyMasterV2Repository.findAllActiveAgencies();
        if (agencyRows == null || agencyRows.isEmpty()) {
            logger.info("No active agencies found in Masters DB");
            return Collections.emptyList();
        }

        List<String> agencyCodes = agencyRows.stream()
                .map(AgencyRow::getAgencyCode)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        List<AgencyPortRow> portRows = agencyMasterV2Repository.findPortsByAgencyCodes(agencyCodes);

        Map<String, List<AgencyPortRow>> portsByAgency = portRows.stream()
                .filter(p -> p.getAgencyCode() != null && !p.getAgencyCode().isEmpty())
                .collect(Collectors.groupingBy(AgencyPortRow::getAgencyCode));

        List<AgencyResponse> result = agencyRows.stream()
                .map(row -> toAgencyResponse(row, portsByAgency))
                .collect(Collectors.toList());
        logger.debug("Mapped {} agency rows to response", result.size());
        return result;
    }

    private AgencyResponse toAgencyResponse(AgencyRow row, Map<String, List<AgencyPortRow>> portsByAgency) {
        AgencyResponse r = new AgencyResponse();
        r.setCode(emptyToNull(row.getAgencyCode()));
        r.setName(emptyToNull(row.getAgencyName()));
        r.setType(mapAgencyType(row.getType()));
        r.setAddress(buildAddress(row));
        r.setEmail(emptyToNull(row.getEmail()));
        r.setUnPortCode(emptyToNull(row.getLocation()));
        r.setControllingPorts(buildPorts(row.getAgencyCode(), portsByAgency));
        return r;
    }

    private AddressResponse buildAddress(AgencyRow row) {
        AddressResponse addr = new AddressResponse();
        addr.setLines(splitAddressLines(row.getAddress()));
        addr.setLocality(emptyToNull(row.getCity()));
        addr.setCountryCode(emptyToNull(row.getCountryCode()));
        return addr;
    }

    private List<AgencyPortResponse> buildPorts(String agencyCode, Map<String, List<AgencyPortRow>> portsByAgency) {
        if (agencyCode == null) {
            return Collections.emptyList();
        }
        return portsByAgency
                .getOrDefault(agencyCode, Collections.emptyList())
                .stream()
                .map(this::toPortResponse)
                .collect(Collectors.toList());
    }

    private AgencyPortResponse toPortResponse(AgencyPortRow row) {
        AgencyPortResponse p = new AgencyPortResponse();
        p.setCode(emptyToNull(row.getPortCode()));
        p.setValidFrom(emptyToNull(row.getValidFrom()));
        p.setValidTo(emptyToNull(row.getValidTo()));
        p.setType(mapPortType(row.getType()));
        return p;
    }

    private List<String> splitAddressLines(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return Collections.singletonList("");
        }
        String t = raw.trim();
        List<String> parts;
        if (t.contains("\n")) {
            parts = Arrays.stream(t.split("\\r?\\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } else {
            int idx = t.indexOf(", ");
            if (idx < 0) {
                parts = Collections.singletonList(t);
            } else {
                parts = new ArrayList<>(2);
                String first = t.substring(0, idx).trim();
                String second = t.substring(idx + 2).trim();
                if (!first.isEmpty()) {
                    parts.add(first);
                }
                if (!second.isEmpty()) {
                    parts.add(second);
                }
            }
        }
        if (parts.size() > 2) {
            parts = new ArrayList<>(parts.subList(0, 2));
        }
        if (parts.isEmpty()) {
            return Collections.singletonList("");
        }
        return parts;
    }

    private String mapAgencyType(String dbChar) {
        String t = emptyToNull(dbChar);
        if (t == null) {
            return null;
        }
        if (t.length() != 1) {
            logger.warn("Unrecognised agency type (expected single char): [{}]", dbChar);
            return null;
        }
        switch (Character.toUpperCase(t.charAt(0))) {
            case 'O':
                return "Office";
            case 'C':
                return "Corporate";
            case 'B':
                return "Branch";
            case 'K':
                return "Back Office";
            default:
                logger.warn("Unrecognised agency type value: [{}]", t);
                return null;
        }
    }

    private String mapPortType(String dbValue) {
        String t = emptyToNull(dbValue);
        if (t == null) {
            return null;
        }
        String u = t.toUpperCase();
        switch (u) {
            case "I":
                return "Import";
            case "E":
                return "Export";
            case "IMPORT":
                return "Import";
            case "EXPORT":
                return "Export";
            default:
                logger.warn("Unrecognised agency port type value: [{}]", t);
                return null;
        }
    }

    private static String emptyToNull(String s) {
        if (s == null) {
            return null;
        }
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed;
    }
}
