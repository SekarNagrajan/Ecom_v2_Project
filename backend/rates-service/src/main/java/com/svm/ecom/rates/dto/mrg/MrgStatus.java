package com.svm.ecom.rates.dto.mrg;

import java.util.ArrayList;
import java.util.List;

/**
 * Status codes stored in {@code MRGMaster.status} / {@code MRGDetail.status}.
 * Only {@link #ACTIVE} and {@link #DRAFT} MRGs are exposed by {@code GET /api/v1/rates/mrg}
 * (other codes present in the data, e.g. V and I, are not published).
 */
public enum MrgStatus {

    ACTIVE("A"),
    DRAFT("D");

    private final String code;

    MrgStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /** DB codes of every status exposed by the API, in declaration order: ["A", "D"]. */
    public static List<String> exposedCodes() {
        List<String> codes = new ArrayList<String>();
        for (MrgStatus s : values()) {
            codes.add(s.code);
        }
        return codes;
    }
}
