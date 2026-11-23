package com.svm.ecom.rates.repository;

import com.svm.ecom.rates.dto.mrg.MrgDetailLine;
import com.svm.ecom.rates.dto.mrg.MrgStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Read-only access to the MRG (Minimum Rate Guideline) tables {@code MRGMaster} / {@code MRGDetail}
 * in the Commercial database.
 */
@Repository
public class MrgRepository {

    private static final String MASTER_SQL_HEAD =
            "SELECT TOP (?) m.mrgCode, m.reference FROM MRGMaster m (NOLOCK) WHERE ";

    // mrgQuery. Selects every column of the original query plus Expfree / Impfree / Expstofree / Impstofree.
    // Response mapping (verified against the MRG module screen / export):
    //   extRateLoc = LocAmt, extRateUsd = tariffAmt, lumpsumLoc = LpLocAmt, lumpsumUsd = lumpSum,
    //   expDetention/impDetention = Expfree/Impfree, expDemurrage/impDemurrage = Expstofree/Impstofree.
    //   The legacy Expdettariff/Impdettariff/Expdemtariff/Impdemtariff columns are still selected
    //   (aliased *Tariff) but are NOT published: they hold different values from what the MRG module displays.
    private static final String DETAIL_SQL_HEAD =
            "SELECT D.mrgCode, D.fmDate, D.toDate, "
                    + "D.fmRegion AS fromTca, D.fmCountry AS fromCountry, D.origin AS origin, D.loadPort AS pol, "
                    + "D.toRegion AS toTca, D.toCountry AS toCountry, D.dischPort AS pod, D.delivery AS delivery, "
                    + "D.eqpType AS eqpType, D.Tservice AS transportService, D.currency AS currency, "
                    + "D.Internalrate AS intRateLoc, D.InternalRateUSD AS intRateUsd, "
                    + "D.LocAmt AS extRateLoc, D.tariffAmt AS extRateUsd, "
                    + "D.discretType AS discreType, D.LpLocAmt AS lumpsumLoc, D.lumpSum AS lumpsumUsd, "
                    + "D.formula AS formula, D.remarks AS remarks, D.CommodityCode AS commodity, "
                    + "D.soc AS soc, D.inclusive AS inclusive, D.subjectto AS subjectTo, D.dg AS dg, "
                    + "D.Expdettariff AS expDetentionTariff, D.Impdettariff AS impDetentionTariff, "
                    + "D.Expdemtariff AS expDemurrageTariff, D.Impdemtariff AS impDemurrageTariff, "
                    + "D.Expfree AS expDetention, D.Impfree AS impDetention, "
                    + "D.Expstofree AS expDemurrage, D.Impstofree AS impDemurrage "
                    + "FROM MRGDetail D (NOLOCK) WHERE ";

    private final JdbcTemplate commercialJdbcTemplate;

    public MrgRepository(@Qualifier("commercialJdbcTemplate") JdbcTemplate commercialJdbcTemplate) {
        this.commercialJdbcTemplate = commercialJdbcTemplate;
    }

    /**
     * Keyset page of MRG headers ordered by {@code mrgCode ASC}.
     *
     * @param afterMrgCode exclusive lower bound (last code of the previous page) or {@code null}
     * @param fetchSize    {@code limit + 1} so the caller can detect {@code hasNext}
     *
     * Only MRGs whose {@code status} is a {@link MrgStatus} (A / D) are returned.
     */
    public List<MasterRow> findMasterPage(String afterMrgCode, int fetchSize) {
        List<String> statuses = MrgStatus.exposedCodes();
        List<Object> args = new ArrayList<Object>();
        args.add(fetchSize);
        StringBuilder sql = new StringBuilder(MASTER_SQL_HEAD);
        sql.append("m.status IN (").append(placeholders(statuses.size(), "varchar(1)")).append(")");
        args.addAll(statuses);
        if (afterMrgCode != null) {
            sql.append(" AND m.mrgCode > CAST(? AS varchar(15))");
            args.add(afterMrgCode);
        }
        sql.append(" ORDER BY m.mrgCode ASC");
        return commercialJdbcTemplate.query(sql.toString(), args.toArray(), (ResultSet rs, int i) ->
                new MasterRow(rs.getString("mrgCode"), rs.getString("reference")));
    }

    /**
     * All detail lines for the given MRG codes (only lines whose {@code status} is a {@link MrgStatus}: A / D),
     * ordered by mrgCode, itemno, id.
     */
    public List<DetailRow> findDetails(List<String> mrgCodes) {
        if (mrgCodes == null || mrgCodes.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> detailStatuses = MrgStatus.exposedCodes();
        List<Object> args = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder(DETAIL_SQL_HEAD);
        sql.append("D.mrgCode IN (").append(placeholders(mrgCodes.size(), "varchar(15)")).append(")");
        args.addAll(mrgCodes);
        sql.append(" AND D.status IN (").append(placeholders(detailStatuses.size(), "varchar(1)")).append(")");
        args.addAll(detailStatuses);
        sql.append(" ORDER BY D.mrgCode ASC, D.itemno ASC, D.id ASC");
        return commercialJdbcTemplate.query(sql.toString(), args.toArray(), (rs, i) -> mapDetail(rs));
    }

    private static String placeholders(int count, String sqlType) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append("CAST(? AS ").append(sqlType).append(')');
        }
        return sb.toString();
    }

    private static DetailRow mapDetail(ResultSet rs) throws SQLException {
        MrgDetailLine line = new MrgDetailLine();
        line.setFromTca(str(rs, "fromTca"));
        line.setFromCountry(str(rs, "fromCountry"));
        line.setOrigin(str(rs, "origin"));
        line.setPol(str(rs, "pol"));
        line.setToTca(str(rs, "toTca"));
        line.setToCountry(str(rs, "toCountry"));
        line.setPod(str(rs, "pod"));
        line.setDelivery(str(rs, "delivery"));
        line.setEqpType(str(rs, "eqpType"));
        line.setTransportService(normalizeTransportService(rs.getString("transportService")));
        line.setCurrency(str(rs, "currency"));
        line.setIntRateLoc(money(rs, "intRateLoc"));
        line.setIntRateUsd(money(rs, "intRateUsd"));
        line.setExtRateLoc(money(rs, "extRateLoc"));
        line.setExtRateUsd(money(rs, "extRateUsd"));
        String disc = str(rs, "discreType");
        line.setDiscreType(disc == null || disc.isEmpty() ? "N" : disc.toUpperCase());
        line.setLumpsumLoc(money(rs, "lumpsumLoc"));
        line.setLumpsumUsd(money(rs, "lumpsumUsd"));
        line.setFormula(nullToEmpty(str(rs, "formula")));
        line.setRemarks(nullToEmpty(str(rs, "remarks")));
        line.setCommodity(nullToEmpty(str(rs, "commodity")));
        line.setSoc(flag(rs.getString("soc")));
        line.setInclusive(csv(rs.getString("inclusive")));
        line.setSubjectTo(csv(rs.getString("subjectTo")));
        line.setDg(flag(rs.getString("dg")));
        line.setExpDetention(integer(rs, "expDetention"));
        line.setImpDetention(integer(rs, "impDetention"));
        line.setExpDemurrage(integer(rs, "expDemurrage"));
        line.setImpDemurrage(integer(rs, "impDemurrage"));
        return new DetailRow(rs.getString("mrgCode"), rs.getTimestamp("fmDate"), rs.getTimestamp("toDate"), line);
    }

    private static String str(ResultSet rs, String col) throws SQLException {
        String v = rs.getString(col);
        return v == null ? null : v.trim();
    }

    private static String nullToEmpty(String v) {
        return v == null ? "" : v;
    }

    /** float column -> fixed 2-decimals string (e.g. "500.00"); NULL stays null. */
    private static String money(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        if (rs.wasNull()) {
            return null;
        }
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static Integer integer(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : Integer.valueOf(v);
    }

    /** Same rule as the legacy query: 'Y' or '1' means true. */
    private static boolean flag(String v) {
        if (v == null) {
            return false;
        }
        String t = v.trim();
        return "Y".equalsIgnoreCase(t) || "1".equals(t);
    }

    /** "ADM, BAF ,CAF" -> ["ADM","BAF","CAF"]; null/blank -> []. Duplicates removed, order kept. */
    static List<String> csv(String v) {
        if (v == null || v.trim().isEmpty()) {
            return new ArrayList<String>();
        }
        Set<String> out = new LinkedHashSet<String>();
        for (String part : v.split(",")) {
            String p = part.trim();
            if (!p.isEmpty()) {
                out.add(p);
            }
        }
        return new ArrayList<String>(out);
    }

    /**
     * Data holds both "CY / CY" and "CY/CY" (and "FREE IN  /  CY"); the API contract uses the compact form.
     */
    static String normalizeTransportService(String v) {
        if (v == null) {
            return null;
        }
        return v.trim().replaceAll("\\s*/\\s*", "/").replaceAll("\\s+", " ");
    }

    public static final class MasterRow {
        private final String mrgCode;
        private final String reference;

        public MasterRow(String mrgCode, String reference) {
            this.mrgCode = mrgCode;
            this.reference = reference;
        }

        public String getMrgCode() { return mrgCode; }
        public String getReference() { return reference; }
    }

    public static final class DetailRow {
        private final String mrgCode;
        private final Timestamp fromDate;
        private final Timestamp toDate;
        private final MrgDetailLine line;

        public DetailRow(String mrgCode, Timestamp fromDate, Timestamp toDate, MrgDetailLine line) {
            this.mrgCode = mrgCode;
            this.fromDate = fromDate;
            this.toDate = toDate;
            this.line = line;
        }

        public String getMrgCode() { return mrgCode; }
        public Timestamp getFromDate() { return fromDate; }
        public Timestamp getToDate() { return toDate; }
        public MrgDetailLine getLine() { return line; }
    }
}
