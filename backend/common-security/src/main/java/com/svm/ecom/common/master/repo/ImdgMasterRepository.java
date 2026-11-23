package com.svm.ecom.common.master.repo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared IMDG detail lookups against Documentation DB tables used by LRP DG Creation
 * ({@code subs}, {@code stowseg}, {@code dgamendment}, {@code DgSegregation}).
 * Used by Master V2 HTTP and Booking DG validate/enrich.
 */
@Repository
public class ImdgMasterRepository {

    /**
     * Same column set as LRP {@code getUnoDetails} / {@code getvariantdetails}.
     */
    private static final String SQL_BY_UNNO =
            "SELECT unno, variant, class, sublabel1, sublabel2, pg, ems, psn, techname, "
                    + "stow, dglphrase, STOWCAT, state "
                    + "FROM subs (NOLOCK) "
                    + "WHERE unno = ?";

    private static final String SQL_BY_UNNO_AND_VARIANT =
            SQL_BY_UNNO + " AND variant = ?";

    private static final String SQL_CURRENT_AMENDMENT =
            "SELECT TOP 1 amendment FROM dgamendment (NOLOCK) "
                    + "WHERE GETDATE() BETWEEN startdate AND enddate AND status = 'A'";

    private static final String SQL_STOW_TEXT =
            "SELECT dgltext FROM stowseg (NOLOCK) WHERE dglphrase = ?";

    private static final String SQL_SEGREGATION_BY_UNNO =
            "SELECT DISTINCT segregation FROM DgSegregation (NOLOCK) WHERE unno = ?";

    private final JdbcTemplate documentationJdbcTemplate;

    public ImdgMasterRepository(
            @Qualifier("documentationJdbcTemplate") JdbcTemplate documentationJdbcTemplate) {
        this.documentationJdbcTemplate = documentationJdbcTemplate;
    }

    public List<ImdgRow> findByUnNo(String unNo, String variant) {
        if (unNo == null || unNo.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String trimmedUnNo = unNo.trim();
        if (variant != null && !variant.trim().isEmpty()) {
            return documentationJdbcTemplate.query(
                    SQL_BY_UNNO_AND_VARIANT,
                    (rs, rowNum) -> mapRow(rs),
                    trimmedUnNo,
                    variant.trim());
        }
        return documentationJdbcTemplate.query(SQL_BY_UNNO, (rs, rowNum) -> mapRow(rs), trimmedUnNo);
    }

    public String findCurrentAmendment() {
        List<String> rows = documentationJdbcTemplate.query(
                SQL_CURRENT_AMENDMENT,
                (rs, rowNum) -> emptyToNull(rs.getString(1)));
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * Resolves segregation text for a UN number from {@code DgSegregation}.
     */
    public String findSegregationByUnNo(String unNo) {
        if (unNo == null || unNo.trim().isEmpty()) {
            return null;
        }
        List<String> values = documentationJdbcTemplate.query(
                SQL_SEGREGATION_BY_UNNO,
                (rs, rowNum) -> emptyToNull(rs.getString(1)),
                unNo.trim());
        if (values == null || values.isEmpty()) {
            return null;
        }
        Set<String> seen = new LinkedHashSet<>();
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            if (v == null || !seen.add(v)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(v);
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    /**
     * Resolves stowage instruction text from {@code stowseg} using space-separated dglphrase tokens,
     * matching LRP DG Creation behaviour.
     */
    public String resolveStowageInstruction(String dglPhrase) {
        if (dglPhrase == null || dglPhrase.trim().isEmpty()) {
            return null;
        }
        Set<String> seenPhrase = new LinkedHashSet<>();
        Set<String> seenText = new LinkedHashSet<>();
        StringBuilder sb = new StringBuilder();
        for (String token : dglPhrase.trim().split("\\s+")) {
            if (token == null || token.trim().isEmpty() || !seenPhrase.add(token.trim())) {
                continue;
            }
            List<String> texts = documentationJdbcTemplate.query(
                    SQL_STOW_TEXT,
                    (rs, rowNum) -> emptyToNull(rs.getString(1)),
                    token.trim());
            for (String text : texts) {
                if (text == null || !seenText.add(text)) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(text);
            }
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    private ImdgRow mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        ImdgRow row = new ImdgRow();
        row.setUnNo(emptyToNull(rs.getString("unno")));
        row.setVariant(emptyToNull(rs.getString("variant")));
        row.setImoClass(emptyToNull(rs.getString("class")));
        row.setSubRisk1(emptyToNull(rs.getString("sublabel1")));
        row.setSubRisk2(emptyToNull(rs.getString("sublabel2")));
        row.setPackagingGroup(emptyToNull(rs.getString("pg")));
        row.setEms(emptyToNull(rs.getString("ems")));
        row.setProperShippingName(emptyToNull(rs.getString("psn")));
        row.setTechnicalName(emptyToNull(rs.getString("techname")));
        row.setStow(emptyToNull(rs.getString("stow")));
        row.setDglPhrase(emptyToNull(rs.getString("dglphrase")));
        row.setImoStow(emptyToNull(rs.getString("STOWCAT")));
        row.setState(emptyToNull(rs.getString("state")));
        return row;
    }

    private static String emptyToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /** Raw IMDG row from {@code subs}. */
    public static final class ImdgRow {
        private String unNo;
        private String variant;
        private String imoClass;
        private String subRisk1;
        private String subRisk2;
        private String packagingGroup;
        private String ems;
        private String properShippingName;
        private String technicalName;
        private String stow;
        private String dglPhrase;
        private String imoStow;
        private String state;

        public String getUnNo() { return unNo; }
        public void setUnNo(String unNo) { this.unNo = unNo; }
        public String getVariant() { return variant; }
        public void setVariant(String variant) { this.variant = variant; }
        public String getImoClass() { return imoClass; }
        public void setImoClass(String imoClass) { this.imoClass = imoClass; }
        public String getSubRisk1() { return subRisk1; }
        public void setSubRisk1(String subRisk1) { this.subRisk1 = subRisk1; }
        public String getSubRisk2() { return subRisk2; }
        public void setSubRisk2(String subRisk2) { this.subRisk2 = subRisk2; }
        public String getPackagingGroup() { return packagingGroup; }
        public void setPackagingGroup(String packagingGroup) { this.packagingGroup = packagingGroup; }
        public String getEms() { return ems; }
        public void setEms(String ems) { this.ems = ems; }
        public String getProperShippingName() { return properShippingName; }
        public void setProperShippingName(String properShippingName) { this.properShippingName = properShippingName; }
        public String getTechnicalName() { return technicalName; }
        public void setTechnicalName(String technicalName) { this.technicalName = technicalName; }
        public String getStow() { return stow; }
        public void setStow(String stow) { this.stow = stow; }
        public String getDglPhrase() { return dglPhrase; }
        public void setDglPhrase(String dglPhrase) { this.dglPhrase = dglPhrase; }
        public String getImoStow() { return imoStow; }
        public void setImoStow(String imoStow) { this.imoStow = imoStow; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }
}
