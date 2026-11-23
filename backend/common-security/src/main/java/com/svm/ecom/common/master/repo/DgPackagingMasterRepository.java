package com.svm.ecom.common.master.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared DG packaging master lookups against Documentation DB {@code inoutpackagecode}.
 * Used by Master V2 HTTP and Booking DG validate/enrich.
 */
@Repository
public class DgPackagingMasterRepository {

    private static final String SQL_BASE =
            "SELECT DISTINCT Code, Name, type "
                    + "FROM inoutpackagecode (NOLOCK) "
                    + "WHERE status = 'A'";

    private final JdbcTemplate documentationJdbcTemplate;

    public DgPackagingMasterRepository(
            @Qualifier("documentationJdbcTemplate") JdbcTemplate documentationJdbcTemplate) {
        this.documentationJdbcTemplate = documentationJdbcTemplate;
    }

    /**
     * @param usageDbFilter exact DB type {@code o}, {@code i}, or {@code b}. Null = all.
     * @param code          exact packaging code filter (optional)
     */
    public List<DgPackagingRow> findAll(String usageDbFilter, String code) {
        StringBuilder sql = new StringBuilder(SQL_BASE);
        List<Object> args = new ArrayList<>();

        if (usageDbFilter != null) {
            // Exact type only: OUTER→o, INNER→i, BOTH→b (no cross-include)
            sql.append(" AND type = ?");
            args.add(usageDbFilter);
        }

        if (code != null && !code.trim().isEmpty()) {
            sql.append(" AND Code = ?");
            args.add(code.trim());
        }

        sql.append(" ORDER BY Code");

        return documentationJdbcTemplate.query(
                sql.toString(),
                (rs, rowNum) -> {
                    DgPackagingRow row = new DgPackagingRow();
                    row.setCode(emptyToNull(rs.getString("Code")));
                    row.setName(emptyToNull(rs.getString("Name")));
                    row.setPackType(emptyToNull(rs.getString("type")));
                    return row;
                },
                args.toArray());
    }

    /**
     * Lookup a single active packaging row by code (any type).
     */
    public DgPackagingRow findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        List<DgPackagingRow> rows = findAll(null, code.trim());
        return rows.isEmpty() ? null : rows.get(0);
    }

    private static String emptyToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    public static final class DgPackagingRow {
        private String code;
        private String name;
        private String packType;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPackType() { return packType; }
        public void setPackType(String packType) { this.packType = packType; }
    }
}
