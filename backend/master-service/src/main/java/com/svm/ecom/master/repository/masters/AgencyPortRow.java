package com.svm.ecom.master.repository.masters;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Row projection from {@code agencylocationsnew} for controlling ports.
 */
public class AgencyPortRow {

    private final String agencyCode;
    private final String portCode;
    private final String type;
    private final String validFrom;
    private final String validTo;

    /**
     * @param row native query row: agencycode, portcode, type, validfrom, validto
     */
    public AgencyPortRow(Object[] row) {
        if (row == null || row.length < 5) {
            throw new IllegalArgumentException("Expected 5 columns from agencylocationsnew query");
        }
        this.agencyCode = strCol(row, 0);
        this.portCode = strCol(row, 1);
        this.type = strCol(row, 2);
        this.validFrom = row[3] == null ? null : toDateString(row[3]);
        this.validTo = row[4] == null ? null : toDateString(row[4]);
    }

    private static String strCol(Object[] row, int i) {
        if (row[i] == null) {
            return null;
        }
        return String.valueOf(row[i]).trim();
    }

    static String toDateString(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Timestamp) {
            return ((Timestamp) o).toLocalDateTime().toLocalDate().toString();
        }
        if (o instanceof java.sql.Date) {
            return o.toString();
        }
        if (o instanceof Date) {
            return new java.sql.Date(((Date) o).getTime()).toString();
        }
        if (o instanceof String) {
            String s = ((String) o).trim();
            if (s.isEmpty()) {
                return null;
            }
            return s.length() > 10 ? s.substring(0, 10) : s;
        }
        if (o instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) o).toLocalDate().toString();
        }
        if (o instanceof java.time.LocalDate) {
            return o.toString();
        }
        String s = String.valueOf(o).trim();
        if (s.isEmpty()) {
            return null;
        }
        return s.length() > 10 ? s.substring(0, 10) : s;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public String getPortCode() {
        return portCode;
    }

    public String getType() {
        return type;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public String getValidTo() {
        return validTo;
    }
}
