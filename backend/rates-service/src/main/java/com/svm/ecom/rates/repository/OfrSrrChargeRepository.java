package com.svm.ecom.rates.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * JDBC equivalent of Booking OFR charge reads:
 * <ul>
 *   <li>Commercial DB — {@code getOFRDetail} / {@code getSRRDetail} (SRR, CRA, SER, TEN, TAR, FMC)</li>
 *   <li>Report DB — CMS / SCR / TAF / SRA via {@code svcratedetail} + {@code svcratesurcharge}
 *       (Booking {@code GetOFRDetailNew} contract family)</li>
 * </ul>
 */
@Repository
public class OfrSrrChargeRepository {

    private static final Logger logger = LoggerFactory.getLogger(OfrSrrChargeRepository.class);

    private final JdbcTemplate commercialJdbcTemplate;
    private final JdbcTemplate reportJdbcTemplate;

    public OfrSrrChargeRepository(@Qualifier("commercialJdbcTemplate") JdbcTemplate commercialJdbcTemplate,
                                  @Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        this.commercialJdbcTemplate = commercialJdbcTemplate;
        this.reportJdbcTemplate = reportJdbcTemplate;
    }

    /**
     * Booking {@code getOFRDetail} SRR OFR — by srr + ord only (no port/date).
     */
    public List<Map<String, Object>> findSrrOfrByOrd(String srrNo, String ordNo) {
        String sql = "select s.eqptype, s.ordNo, s.charge, c.chargename, s.currency, s.usdamt, "
                + "s.counterby, s.counterno "
                + "from srrdetail s (NOLOCK) "
                + "inner join srrmaster m (NOLOCK) on s.srrno = m.srrno "
                + "left join charges c (NOLOCK) on s.charge = c.chargecode "
                + "where s.srrno = ? and s.quotestatus = 'A' and s.tos = 'N' "
                + "and s.ordNo = ? and s.charge = 'OFR' and m.status <> 'PE'";
        try {
            return commercialJdbcTemplate.queryForList(sql, srrNo, ordNo);
        } catch (DataAccessException e) {
            logger.warn("SRR OFR-by-ord failed srrNo={}, ordNo={}: {}", srrNo, ordNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * SRR OFR by srrno only (discover ord when request amendmentNumber/ports/date miss).
     * Optional eqptype filter when provided.
     */
    public List<Map<String, Object>> findSrrOfrBySrrNo(String srrNo, String eqpType) {
        String sql = "select s.eqptype, s.ordNo, s.charge, c.chargename, s.currency, s.usdamt, "
                + "s.counterby, s.counterno "
                + "from srrdetail s (NOLOCK) "
                + "inner join srrmaster m (NOLOCK) on s.srrno = m.srrno "
                + "left join charges c (NOLOCK) on s.charge = c.chargecode "
                + "where s.srrno = ? and s.quotestatus = 'A' and s.tos = 'N' "
                + "and s.charge = 'OFR' and m.status <> 'PE' "
                + "and (? is null or s.eqptype = ?)";
        try {
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, srrNo, eqpType, eqpType);
            logger.debug("SRR OFR by srrno: srrNo={}, eqp={}, rows={}", srrNo, eqpType, rows.size());
            return rows;
        } catch (DataAccessException e) {
            logger.warn("SRR OFR-by-srrno failed srrNo={}: {}", srrNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Booking {@code getOFRDetail} related charges (counter-linked; amount = localamt).
     */
    public List<Map<String, Object>> findSrrRelatedByCounter(String srrNo, String ordNo,
                                                             String counterBy, int counterNo) {
        String sql = "select s.eqptype, s.charge, c.chargename, s.currency, s.localamt as usdamt "
                + "from srrdetail s (NOLOCK) "
                + "left join charges c (NOLOCK) on s.charge = c.chargecode "
                + "where s.srrno = ? and s.tos = 'N' and s.ordNo = ? "
                + "and s.charge <> 'OFR' and s.counterby = ? and s.counterno = ?";
        try {
            return commercialJdbcTemplate.queryForList(sql, srrNo, ordNo, counterBy, counterNo);
        } catch (DataAccessException e) {
            logger.warn("SRR related-by-counter failed srrNo={}, ordNo={}: {}", srrNo, ordNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Booking {@code getSRRDetail}: distinct eqp + ord for port pair / validity.
     */
    public List<Map<String, Object>> findSrrEqpOrd(String srrNo, String loadPort, String dischPort,
                                                   String validityDate) {
        String sql = "select distinct eqptype, ordNo, counterNo, counterby "
                + "from srrdetail (NOLOCK) "
                + "where srrno = ? and loadport = ? and dischport = ? "
                + "and quotestatus <> 'R' and tos = 'N' "
                + "and validfrom <= ? and validto >= ?";
        try {
            return commercialJdbcTemplate.queryForList(
                    sql, srrNo, loadPort, dischPort, validityDate, validityDate);
        } catch (DataAccessException e) {
            logger.warn("SRR eqp/ord lookup failed srrNo={}, pol={}, pod={}: {}",
                    srrNo, loadPort, dischPort, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Booking {@code getSRRDetail} OFR with ports + validity + eqp + ord.
     */
    public List<Map<String, Object>> findSrrOfrByPorts(String srrNo, String loadPort, String dischPort,
                                                       String validityDate, String eqpType, String ordNo) {
        String sql = "select distinct s.eqptype, s.charge, c.chargename, s.currency, s.usdamt, "
                + "s.counterno, s.counterby "
                + "from srrdetail s (NOLOCK) "
                + "left join charges c (NOLOCK) on s.charge = c.chargecode "
                + "where s.srrno = ? and s.loadport = ? and s.dischport = ? "
                + "and s.quotestatus = 'A' and s.tos = 'N' "
                + "and s.validfrom <= ? and s.validto >= ? "
                + "and s.eqptype = ? and s.ordNo = ? and s.charge = 'OFR'";
        try {
            return commercialJdbcTemplate.queryForList(
                    sql, srrNo, loadPort, dischPort, validityDate, validityDate, eqpType, ordNo);
        } catch (DataAccessException e) {
            logger.warn("SRR OFR-by-ports failed srrNo={}, ordNo={}, eqp={}: {}",
                    srrNo, ordNo, eqpType, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Booking {@code getSRRDetail} non-OFR (quotestatus I) — ADD/ADL/THD/THL etc.
     * Amount column is {@code usdamt} (Booking maps it to charge amount as-is).
     */
    public List<Map<String, Object>> findSrrRelatedByPorts(String srrNo, String loadPort, String dischPort,
                                                           String validityDate, String eqpType, String ordNo,
                                                           int counterNo, String counterBy) {
        String sql = "select distinct s.eqptype, s.charge, c.chargename, s.currency, s.usdamt "
                + "from srrdetail s (NOLOCK) "
                + "left join charges c (NOLOCK) on s.charge = c.chargecode "
                + "where s.srrno = ? and s.loadport = ? and s.dischport = ? "
                + "and s.quotestatus = 'I' and s.tos = 'N' "
                + "and s.validfrom <= ? and s.validto >= ? "
                + "and s.eqptype = ? and s.ordNo = ? "
                + "and s.counterno = ? and s.counterby = ? and s.charge <> 'OFR'";
        try {
            return commercialJdbcTemplate.queryForList(
                    sql, srrNo, loadPort, dischPort, validityDate, validityDate,
                    eqpType, ordNo, counterNo, counterBy);
        } catch (DataAccessException e) {
            logger.warn("SRR related-by-ports failed srrNo={}, ordNo={}, eqp={}: {}",
                    srrNo, ordNo, eqpType, e.getMessage());
            return Collections.emptyList();
        }
    }

    /** CRA / SER ocean freight from {@code corporatedetailN}. */
    public List<Map<String, Object>> findCraOfr(String corporateNo, String itemNo) {
        String sql = "select 'OFR' as charge, c.chargename, locCurrency as currency, locAmt as usdamt "
                + "from corporatedetailN (NOLOCK) "
                + "left join charges c (NOLOCK) on c.chargecode = 'OFR' "
                + "where corporateNo = ? and itemNo = ? and statusflag <> 'V'";
        try {
            return commercialJdbcTemplate.queryForList(sql, corporateNo, itemNo);
        } catch (DataAccessException e) {
            logger.warn("CRA OFR lookup failed for corporateNo={}, itemNo={}: {}",
                    corporateNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /** CRA / SER related surcharges. */
    public List<Map<String, Object>> findCraRelated(String corporateNo, String itemNo) {
        String sql = "select cs.chargecode as charge, c.chargename, cs.currency, cs.amount as usdamt "
                + "from corporateSurcharge cs (NOLOCK) "
                + "left join charges c (NOLOCK) on cs.chargecode = c.chargecode "
                + "where cs.statusflag <> 'V' and cs.corporateNo = ? and cs.itemNo = ? and cs.vatos = 'N'";
        try {
            return commercialJdbcTemplate.queryForList(sql, corporateNo, itemNo);
        } catch (DataAccessException e) {
            logger.warn("CRA related-charge lookup failed for corporateNo={}, itemNo={}: {}",
                    corporateNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Booking {@code getOFRDetail} TAR/FMC OFR from {@code bookingrates_tariff}.
     */
    public List<Map<String, Object>> findTarOfr(String rateNo, String itemNo) {
        String sql = "select distinct 'OFR' as charge, "
                + "(select top 1 chargename from charges (NOLOCK) where chargecode = 'OFR') as chargename, "
                + "currency, localamount as usdamt, eqptype, itemno "
                + "from bookingrates_tariff (NOLOCK) "
                + "where rateno = ? and itemno = ?";
        try {
            return commercialJdbcTemplate.queryForList(sql, rateNo, itemNo);
        } catch (DataAccessException e) {
            // eqptype/itemno columns may differ by DB version — retry minimal select
            logger.warn("TAR OFR lookup (full) failed rateNo={}, itemNo={}: {} — retrying minimal",
                    rateNo, itemNo, e.getMessage());
            return findTarOfrMinimal(rateNo, itemNo);
        }
    }

    public List<Map<String, Object>> findTarOfrMinimal(String rateNo, String itemNo) {
        String sql = "select distinct localamount as usdamt, currency "
                + "from bookingrates_tariff (NOLOCK) where rateno = ? and itemno = ?";
        try {
            List<Map<String, Object>> rows = commercialJdbcTemplate.queryForList(sql, rateNo, itemNo);
            for (Map<String, Object> row : rows) {
                row.put("charge", "OFR");
                row.put("chargename", "OFR");
            }
            return rows;
        } catch (DataAccessException e) {
            logger.warn("TAR OFR minimal lookup failed rateNo={}, itemNo={}: {}",
                    rateNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Discover item numbers for a tariff rate (when amendmentNumber/ord does not match).
     */
    public List<String> findTarItemNos(String rateNo, String loadPort, String dischPort,
                                       String validityDate, String eqpType) {
        String sql = "select distinct itemno from bookingrates_tariff (NOLOCK) "
                + "where rateno = ? "
                + "and (? is null or loadport = ?) "
                + "and (? is null or dischport = ?) "
                + "and (? is null or (validfrom <= ? and validto >= ?)) "
                + "and (? is null or eqptype = ?)";
        try {
            return commercialJdbcTemplate.query(
                    sql,
                    (rs, rowNum) -> rs.getString(1),
                    rateNo,
                    loadPort, loadPort,
                    dischPort, dischPort,
                    validityDate, validityDate, validityDate,
                    eqpType, eqpType);
        } catch (DataAccessException e) {
            logger.warn("TAR itemno discovery failed rateNo={}: {}", rateNo, e.getMessage());
            try {
                return commercialJdbcTemplate.query(
                        "select distinct itemno from bookingrates_tariff (NOLOCK) where rateno = ?",
                        (rs, rowNum) -> rs.getString(1),
                        rateNo);
            } catch (DataAccessException e2) {
                logger.warn("TAR itemno discovery (simple) failed rateNo={}: {}", rateNo, e2.getMessage());
                return Collections.emptyList();
            }
        }
    }

    /**
     * Booking related charges: {@code ratesurcharge} where rateno + itemno + vatos='N'
     * (TEN/TAR — ADD/ADL/THD/THL as separate lines).
     * Rows with {@code vatos='Y'} are contract/all-in; do not load here.
     */
    public List<Map<String, Object>> findRateSurcharges(String rateNo, String itemNo) {
        String sql = "select cs.charge, c.chargename, cs.currency, cs.amount as usdamt, cs.itemno "
                + "from ratesurcharge cs (NOLOCK) "
                + "left join charges c (NOLOCK) on cs.charge = c.chargecode "
                + "where cs.rateno = ? and cs.itemno = ? and cs.vatos = 'N'";
        try {
            List<Map<String, Object>> rows = commercialJdbcTemplate.queryForList(sql, rateNo, itemNo);
            logger.debug("ratesurcharge (vatos=N): rateNo={}, itemNo={}, rows={}", rateNo, itemNo, rows.size());
            return rows;
        } catch (DataAccessException e) {
            logger.warn("ratesurcharge failed rateNo={}, itemNo={}: {}", rateNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Batch {@code ratesurcharge} for one rateno and many itemnos (same filters as
     * {@link #findRateSurcharges}). Returns map key = trimmed itemno.
     */
    public Map<String, List<Map<String, Object>>> findRateSurchargesByItems(
            String rateNo, Collection<String> itemNos) {
        List<String> items = distinctNonBlank(itemNos);
        Map<String, List<Map<String, Object>>> byItem = newEmptyItemMap(items);
        if (rateNo == null || rateNo.trim().isEmpty() || items.isEmpty()) {
            return byItem;
        }
        if (items.size() == 1) {
            byItem.put(items.get(0), findRateSurcharges(rateNo, items.get(0)));
            return byItem;
        }
        String sql = "select cs.charge, c.chargename, cs.currency, cs.amount as usdamt, cs.itemno "
                + "from ratesurcharge cs (NOLOCK) "
                + "left join charges c (NOLOCK) on cs.charge = c.chargecode "
                + "where cs.rateno = ? and cs.vatos = 'N' and cs.itemno in ("
                + placeholders(items.size()) + ")";
        try {
            List<Object> args = new ArrayList<>();
            args.add(rateNo);
            args.addAll(items);
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, args.toArray());
            groupRowsByItemNo(rows, byItem);
            logger.debug("ratesurcharge batch (vatos=N): rateNo={}, items={}, rows={}",
                    rateNo, items.size(), rows.size());
        } catch (DataAccessException e) {
            logger.warn("ratesurcharge batch failed rateNo={}: {} — falling back per item",
                    rateNo, e.getMessage());
            for (String item : items) {
                byItem.put(item, findRateSurcharges(rateNo, item));
            }
        }
        return byItem;
    }

    /**
     * Batch TAR/FMC OFR from {@code bookingrates_tariff} for one rateno + many itemnos.
     * Same columns/filters as {@link #findTarOfr}; map key = trimmed itemno.
     */
    public Map<String, List<Map<String, Object>>> findTarOfrByItems(
            String rateNo, Collection<String> itemNos) {
        List<String> items = distinctNonBlank(itemNos);
        Map<String, List<Map<String, Object>>> byItem = newEmptyItemMap(items);
        if (rateNo == null || rateNo.trim().isEmpty() || items.isEmpty()) {
            return byItem;
        }
        if (items.size() == 1) {
            byItem.put(items.get(0), findTarOfr(rateNo, items.get(0)));
            return byItem;
        }
        String sql = "select distinct 'OFR' as charge, "
                + "(select top 1 chargename from charges (NOLOCK) where chargecode = 'OFR') as chargename, "
                + "currency, localamount as usdamt, eqptype, itemno "
                + "from bookingrates_tariff (NOLOCK) "
                + "where rateno = ? and itemno in (" + placeholders(items.size()) + ")";
        try {
            List<Object> args = new ArrayList<>();
            args.add(rateNo);
            args.addAll(items);
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, args.toArray());
            groupRowsByItemNo(rows, byItem);
            logger.debug("TAR OFR batch: rateNo={}, items={}, rows={}", rateNo, items.size(), rows.size());
            // Items still empty → same minimal fallback as single-item path
            List<String> missing = new ArrayList<>();
            for (String item : items) {
                if (byItem.get(item) == null || byItem.get(item).isEmpty()) {
                    missing.add(item);
                }
            }
            if (!missing.isEmpty()) {
                Map<String, List<Map<String, Object>>> minimal =
                        findTarOfrMinimalByItems(rateNo, missing);
                for (String item : missing) {
                    List<Map<String, Object>> m = minimal.get(item);
                    if (m != null && !m.isEmpty()) {
                        byItem.put(item, m);
                    }
                }
            }
        } catch (DataAccessException e) {
            logger.warn("TAR OFR batch failed rateNo={}: {} — falling back per item",
                    rateNo, e.getMessage());
            for (String item : items) {
                byItem.put(item, findTarOfr(rateNo, item));
            }
        }
        return byItem;
    }

    private Map<String, List<Map<String, Object>>> findTarOfrMinimalByItems(
            String rateNo, Collection<String> itemNos) {
        List<String> items = distinctNonBlank(itemNos);
        Map<String, List<Map<String, Object>>> byItem = newEmptyItemMap(items);
        if (items.isEmpty()) {
            return byItem;
        }
        String sql = "select distinct localamount as usdamt, currency, itemno "
                + "from bookingrates_tariff (NOLOCK) where rateno = ? and itemno in ("
                + placeholders(items.size()) + ")";
        try {
            List<Object> args = new ArrayList<>();
            args.add(rateNo);
            args.addAll(items);
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, args.toArray());
            for (Map<String, Object> row : rows) {
                row.put("charge", "OFR");
                row.put("chargename", "OFR");
            }
            groupRowsByItemNo(rows, byItem);
        } catch (DataAccessException e) {
            logger.warn("TAR OFR minimal batch failed rateNo={}: {}", rateNo, e.getMessage());
            for (String item : items) {
                byItem.put(item, findTarOfrMinimal(rateNo, item));
            }
        }
        return byItem;
    }

    /**
     * Batch {@code bookingrates} OFR for one rateNo + rateType + amend + many itemnos.
     */
    public Map<String, List<Map<String, Object>>> findBookingRatesOfrByItems(
            String rateNo, String rateType, String amendmentNo, Collection<String> itemNos) {
        List<String> items = distinctNonBlank(itemNos);
        Map<String, List<Map<String, Object>>> byItem = newEmptyItemMap(items);
        if (rateNo == null || rateNo.trim().isEmpty() || items.isEmpty()) {
            return byItem;
        }
        if (items.size() == 1) {
            byItem.put(items.get(0),
                    findBookingRatesOfr(rateNo, items.get(0), rateType, amendmentNo));
            return byItem;
        }
        String sql = "select 'OFR' as charge, "
                + "(select top 1 chargename from charges (NOLOCK) where chargecode = 'OFR') as chargename, "
                + "currency, localamount as usdamt, eqptype, itemno "
                + "from bookingrates (NOLOCK) "
                + "where rateno = ? and ratetype = ? and amendmentno = ? and itemno in ("
                + placeholders(items.size()) + ")";
        try {
            List<Object> args = new ArrayList<>();
            args.add(rateNo);
            args.add(rateType);
            args.add(amendmentNo);
            args.addAll(items);
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, args.toArray());
            groupRowsByItemNo(rows, byItem);
            logger.debug("bookingrates OFR batch: rateNo={}, type={}, amend={}, items={}, rows={}",
                    rateNo, rateType, amendmentNo, items.size(), rows.size());
        } catch (DataAccessException e) {
            logger.warn("bookingrates OFR batch failed rateNo={}: {} — falling back per item",
                    rateNo, e.getMessage());
            for (String item : items) {
                byItem.put(item, findBookingRatesOfr(rateNo, item, rateType, amendmentNo));
            }
        }
        return byItem;
    }

    private static String placeholders(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append('?');
        }
        return sb.toString();
    }

    private static List<String> distinctNonBlank(Collection<String> itemNos) {
        if (itemNos == null || itemNos.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> set = new LinkedHashSet<>();
        for (String item : itemNos) {
            if (item != null && !item.trim().isEmpty()) {
                set.add(item.trim());
            }
        }
        return new ArrayList<>(set);
    }

    private static Map<String, List<Map<String, Object>>> newEmptyItemMap(List<String> items) {
        Map<String, List<Map<String, Object>>> byItem = new HashMap<>();
        for (String item : items) {
            byItem.put(item, new ArrayList<>());
        }
        return byItem;
    }

    private static void groupRowsByItemNo(List<Map<String, Object>> rows,
                                          Map<String, List<Map<String, Object>>> byItem) {
        if (rows == null) {
            return;
        }
        for (Map<String, Object> row : rows) {
            Object raw = row.get("itemno");
            if (raw == null) {
                continue;
            }
            String item = String.valueOf(raw).trim();
            if (item.isEmpty()) {
                continue;
            }
            byItem.computeIfAbsent(item, k -> new ArrayList<>()).add(row);
        }
    }

    /**
     * Booking {@code charges.chargeeqptype}: {@code LP} = lump-sum / shipment (not × container qty);
     * {@code UN}/{@code TEU}/… = per equipment unit.
     */
    public String findChargeEqpType(String chargeCode) {
        if (chargeCode == null || chargeCode.trim().isEmpty()) {
            return "";
        }
        String sql = "select top 1 chargeeqptype from charges (NOLOCK) "
                + "where status = 'A' and chargecode = ?";
        try {
            List<String> rows = commercialJdbcTemplate.query(sql, (rs, rowNum) -> {
                String v = rs.getString(1);
                return v == null ? "" : v.trim();
            }, chargeCode.trim());
            return rows.isEmpty() ? "" : rows.get(0);
        } catch (DataAccessException e) {
            logger.warn("chargeeqptype lookup failed for {}: {}", chargeCode, e.getMessage());
            return "";
        }
    }

    /**
     * Bulk load for {@link com.svm.ecom.rates.service.RateEstimateLookupCacheService}:
     * active {@code chargecode} → {@code chargeeqptype} and {@code chargename} (uppercase keys).
     */
    public ChargeMasterMaps loadAllChargeMaster() {
        String sql = "select chargecode, chargeeqptype, chargename from charges (NOLOCK) "
                + "where status = 'A' and chargecode is not null";
        Map<String, String> eqpTypes = new HashMap<>();
        Map<String, String> names = new HashMap<>();
        try {
            commercialJdbcTemplate.query(sql, rs -> {
                String code = rs.getString("chargecode");
                if (code == null || code.trim().isEmpty()) {
                    return;
                }
                String key = code.trim().toUpperCase(Locale.ROOT);
                String eqp = rs.getString("chargeeqptype");
                eqpTypes.put(key, eqp == null ? "" : eqp.trim());
                String name = rs.getString("chargename");
                names.put(key, name == null ? "" : name.trim());
            });
        } catch (DataAccessException e) {
            logger.warn("bulk charges master load failed: {}", e.getMessage());
        }
        return new ChargeMasterMaps(eqpTypes, names);
    }

    /** Result of {@link #loadAllChargeMaster()}. */
    public static final class ChargeMasterMaps {
        private final Map<String, String> eqpTypeByCode;
        private final Map<String, String> nameByCode;

        public ChargeMasterMaps(Map<String, String> eqpTypeByCode, Map<String, String> nameByCode) {
            this.eqpTypeByCode = eqpTypeByCode != null ? eqpTypeByCode : Collections.emptyMap();
            this.nameByCode = nameByCode != null ? nameByCode : Collections.emptyMap();
        }

        public Map<String, String> getEqpTypeByCode() {
            return eqpTypeByCode;
        }

        public Map<String, String> getNameByCode() {
            return nameByCode;
        }
    }

    /**
     * Bulk load for lookup cache: maps name and code keys → {@code trptsvccode}.
     * Keys include exact name, code, and spaced slash variants (e.g. {@code CY/CY} / {@code CY / CY}).
     */
    public Map<String, String> loadAllTransportServiceKeys() {
        String sql = "select trptsvccode, trptsvcname from transportservice (NOLOCK) where status = 'A' "
                + "and trptsvccode is not null";
        Map<String, String> out = new HashMap<>();
        try {
            commercialJdbcTemplate.query(sql, rs -> {
                String code = rs.getString("trptsvccode");
                if (code == null || code.trim().isEmpty()) {
                    return;
                }
                String trimmedCode = code.trim();
                putTransportKey(out, trimmedCode, trimmedCode);
                String name = rs.getString("trptsvcname");
                if (name != null && !name.trim().isEmpty()) {
                    String trimmedName = name.trim();
                    putTransportKey(out, trimmedName, trimmedCode);
                    String spaced = trimmedName.replace("/", " / ").replaceAll("\\s+", " ").trim();
                    putTransportKey(out, spaced, trimmedCode);
                    String compact = trimmedName.replace(" / ", "/").replaceAll("\\s*/\\s*", "/").trim();
                    putTransportKey(out, compact, trimmedCode);
                }
            });
        } catch (DataAccessException e) {
            logger.warn("bulk transportservice load failed: {}", e.getMessage());
        }
        return out;
    }

    private static void putTransportKey(Map<String, String> map, String key, String code) {
        if (key == null || key.isEmpty()) {
            return;
        }
        map.put(key, code);
        map.put(key.toUpperCase(Locale.ROOT), code);
    }

    /**
     * Fallback when OFR itemno ≠ ratesurcharge.itemno: all related rows for rateno with vatos='N'.
     */
    public List<Map<String, Object>> findRateSurchargesByRateNo(String rateNo) {
        String sql = "select cs.charge, c.chargename, cs.currency, cs.amount as usdamt, cs.itemno "
                + "from ratesurcharge cs (NOLOCK) "
                + "left join charges c (NOLOCK) on cs.charge = c.chargecode "
                + "where cs.rateno = ? and cs.vatos = 'N' "
                + "and (cs.charge is null or cs.charge <> 'OFR')";
        try {
            List<Map<String, Object>> rows = commercialJdbcTemplate.queryForList(sql, rateNo);
            logger.debug("ratesurcharge by rateNo: rateNo={}, rows={}", rateNo, rows.size());
            return rows;
        } catch (DataAccessException e) {
            logger.warn("ratesurcharge by rateNo failed rateNo={}: {}", rateNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /** Distinct itemnos on ratesurcharge for a rate (helps align TAR OFR item ≠ surcharge item). */
    public List<String> findRateSurchargeItemNos(String rateNo) {
        String sql = "select distinct itemno from ratesurcharge (NOLOCK) "
                + "where rateno = ? and vatos = 'N'";
        try {
            return commercialJdbcTemplate.query(sql, (rs, rowNum) -> {
                String v = rs.getString(1);
                return v == null ? "" : v.trim();
            }, rateNo);
        } catch (DataAccessException e) {
            logger.warn("ratesurcharge itemno discovery failed rateNo={}: {}", rateNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /** TEN OFR from {@code bookingrates}. */
    public List<Map<String, Object>> findTenOfr(String rateNo, String itemNo) {
        String sql = "select 'OFR' as charge, "
                + "(select chargename from charges (NOLOCK) where chargecode = 'OFR') as chargename, "
                + "currency, localamount as usdamt "
                + "from bookingrates (NOLOCK) where rateno = ? and itemno = ?";
        try {
            return commercialJdbcTemplate.queryForList(sql, rateNo, itemNo);
        } catch (DataAccessException e) {
            logger.warn("TEN OFR lookup failed rateNo={}, itemNo={}: {}", rateNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Commercial synced OFR ({@code bookingrates}): rateno + itemno + ratetype + amendmentno.
     * Matches CMS/TEN rows written for Booking Auto Rate (GetOFRDetailNew).
     */
    public List<Map<String, Object>> findBookingRatesOfr(String rateNo, String itemNo,
                                                         String rateType, String amendmentNo) {
        String sql = "select 'OFR' as charge, "
                + "(select top 1 chargename from charges (NOLOCK) where chargecode = 'OFR') as chargename, "
                + "currency, localamount as usdamt, eqptype, itemno "
                + "from bookingrates (NOLOCK) "
                + "where rateno = ? and itemno = ? and ratetype = ? and amendmentno = ?";
        try {
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, rateNo, itemNo, rateType, amendmentNo);
            logger.debug("bookingrates OFR: rateNo={}, item={}, type={}, amend={}, rows={}",
                    rateNo, itemNo, rateType, amendmentNo, rows.size());
            return rows;
        } catch (DataAccessException e) {
            logger.warn("bookingrates OFR failed rateNo={}, item={}: {} — retry without eqptype",
                    rateNo, itemNo, e.getMessage());
            return findBookingRatesOfrMinimal(rateNo, itemNo, rateType, amendmentNo);
        }
    }

    private List<Map<String, Object>> findBookingRatesOfrMinimal(String rateNo, String itemNo,
                                                                 String rateType, String amendmentNo) {
        String sql = "select currency, localamount as usdamt "
                + "from bookingrates (NOLOCK) "
                + "where rateno = ? and itemno = ? and ratetype = ? and amendmentno = ?";
        try {
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, rateNo, itemNo, rateType, amendmentNo);
            for (Map<String, Object> row : rows) {
                row.put("charge", "OFR");
                row.put("chargename", "OFR");
            }
            return rows;
        } catch (DataAccessException e) {
            logger.warn("bookingrates OFR minimal failed rateNo={}: {}", rateNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Commercial tariff OFR ({@code bookingrates_tariff}): rateno + itemno + ratetype + amendmentno.
     * Fallback when {@code bookingrates} has no row.
     */
    public List<Map<String, Object>> findTariffOfrByKeys(String rateNo, String itemNo,
                                                         String rateType, String amendmentNo) {
        String sql = "select distinct 'OFR' as charge, "
                + "(select top 1 chargename from charges (NOLOCK) where chargecode = 'OFR') as chargename, "
                + "currency, localamount as usdamt "
                + "from bookingrates_tariff (NOLOCK) "
                + "where rateno = ? and itemno = ? and ratetype = ? and amendmentno = ?";
        try {
            List<Map<String, Object>> rows =
                    commercialJdbcTemplate.queryForList(sql, rateNo, itemNo, rateType, amendmentNo);
            logger.debug("bookingrates_tariff OFR: rateNo={}, item={}, type={}, amend={}, rows={}",
                    rateNo, itemNo, rateType, amendmentNo, rows.size());
            return rows;
        } catch (DataAccessException e) {
            logger.warn("bookingrates_tariff OFR failed rateNo={}, item={}: {}",
                    rateNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    // -------------------------------------------------------------------------
    // Legacy ReportDB CMS helpers (kept unused by service; safe to leave for rollback)
    // -------------------------------------------------------------------------

    /**
     * Discover CMS itemnos for contract + amendment, optionally filtered by ports / eqp / validity.
     */
    public List<String> findCmsItemNos(String contractNo, int amendmentNo, String loadPort, String dischPort,
                                       String validityDate, String eqpType) {
        String sql = "select distinct convert(varchar(50), itemno) as itemno "
                + "from svcratedetail (NOLOCK) "
                + "where contractNo = ? and amendmentNo = ? "
                + "and quotestatus = 'A' and contractstatus in ('A','D') "
                + "and (? is null or eqptype = ?) "
                + "and (? is null or (pol = ? or loadport = ?)) "
                + "and (? is null or (pod = ? or dischport = ?)) "
                + "and (? is null or (lkvalidfrm <= ? and lkvalidto >= ?)) "
                + "order by itemno";
        try {
            List<String> rows = reportJdbcTemplate.query(
                    sql,
                    (rs, rowNum) -> {
                        String v = rs.getString(1);
                        return v == null ? "" : v.trim();
                    },
                    contractNo, amendmentNo,
                    eqpType, eqpType,
                    loadPort, loadPort, loadPort,
                    dischPort, dischPort, dischPort,
                    validityDate, validityDate, validityDate);
            logger.debug("CMS item discovery: contractNo={}, amend={}, eqp={}, pol={}, pod={}, items={}",
                    contractNo, amendmentNo, eqpType, loadPort, dischPort, rows);
            return rows;
        } catch (DataAccessException e) {
            logger.warn("CMS item discovery failed contractNo={}, amend={}: {}",
                    contractNo, amendmentNo, e.getMessage());
            try {
                return reportJdbcTemplate.query(
                        "select distinct convert(varchar(50), itemno) from svcratedetail (NOLOCK) "
                                + "where contractNo = ? and amendmentNo = ? "
                                + "and quotestatus = 'A' and contractstatus in ('A','D')",
                        (rs, rowNum) -> {
                            String v = rs.getString(1);
                            return v == null ? "" : v.trim();
                        },
                        contractNo, amendmentNo);
            } catch (DataAccessException e2) {
                logger.warn("CMS item discovery (simple) failed contractNo={}: {}", contractNo, e2.getMessage());
                return Collections.emptyList();
            }
        }
    }

    /**
     * CMS family OFR from {@code svcratedetail}: prefer {@code allinamt}, else {@code amt}.
     */
    public List<Map<String, Object>> findCmsOfr(String contractNo, int amendmentNo, String itemNo) {
        String sql = "select 'OFR' as charge, 'Ocean Freight' as chargename, "
                + "currency, "
                + "case when allinamt is not null and allinamt <> 0 then allinamt else amt end as usdamt, "
                + "eqptype, convert(varchar(50), itemno) as itemno "
                + "from svcratedetail (NOLOCK) "
                + "where contractNo = ? and amendmentNo = ? and itemno = ? "
                + "and quotestatus = 'A' and contractstatus in ('A','D')";
        try {
            List<Map<String, Object>> rows =
                    reportJdbcTemplate.queryForList(sql, contractNo, amendmentNo, itemNo);
            logger.debug("CMS OFR: contractNo={}, amend={}, itemNo={}, rows={}",
                    contractNo, amendmentNo, itemNo, rows.size());
            return rows;
        } catch (DataAccessException e) {
            logger.warn("CMS OFR failed contractNo={}, amend={}, item={}: {}",
                    contractNo, amendmentNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * CMS family related charges (THL/THD/ADD/ADL) from {@code svcratesurcharge} where {@code allin='N'}.
     * Tries {@code chargecode} first, then {@code charge} (schema variants).
     */
    public List<Map<String, Object>> findCmsRelated(String contractNo, int amendmentNo, String itemNo) {
        List<Map<String, Object>> rows = findCmsRelatedByColumn(contractNo, amendmentNo, itemNo, "chargecode");
        if (rows.isEmpty()) {
            rows = findCmsRelatedByColumn(contractNo, amendmentNo, itemNo, "charge");
        }
        logger.debug("CMS related: contractNo={}, amend={}, itemNo={}, rows={}",
                contractNo, amendmentNo, itemNo, rows.size());
        return rows;
    }

    private List<Map<String, Object>> findCmsRelatedByColumn(String contractNo, int amendmentNo,
                                                             String itemNo, String chargeCol) {
        String sql = "select ss." + chargeCol + " as charge, ss." + chargeCol + " as chargename, "
                + "ss.currency, ss.amount as usdamt, convert(varchar(50), ss.itemno) as itemno "
                + "from svcratesurcharge ss (NOLOCK) "
                + "where ss.contractno = ? and ss.itemno = ? and ss.amendflag = ? "
                + "and ss.allin = 'N' "
                + "and (ss." + chargeCol + " is null or ss." + chargeCol + " <> 'OFR')";
        try {
            return reportJdbcTemplate.queryForList(sql, contractNo, itemNo, amendmentNo);
        } catch (DataAccessException e) {
            logger.warn("CMS related via {} failed contractNo={}, item={}: {}",
                    chargeCol, contractNo, itemNo, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Booking {@code TxtTransServiceCode}: resolve {@code transportservice.trptsvccode}
     * from request transport name (e.g. {@code CY/CY}) for {@code surchargeVO.termservice[0]}.
     * Also accepts an already-resolved code. Empty / miss → empty string (caller may fall back).
     * Prefer {@link com.svm.ecom.rates.service.RateEstimateLookupCacheService} on the estimate path.
     */
    public String findTransportServiceCode(String transportServiceNameOrCode) {
        if (transportServiceNameOrCode == null || transportServiceNameOrCode.trim().isEmpty()) {
            return "";
        }
        String input = transportServiceNameOrCode.trim();
        String sql = "select top 1 trptsvccode from transportservice (NOLOCK) "
                + "where status = 'A' and (trptsvcname = ? or trptsvccode = ?)";
        try {
            List<String> rows = commercialJdbcTemplate.query(sql, (rs, rowNum) -> {
                String v = rs.getString(1);
                return v == null ? "" : v.trim();
            }, input, input);
            if (!rows.isEmpty() && rows.get(0) != null && !rows.get(0).isEmpty()) {
                return rows.get(0);
            }
            // Request often "CY/CY"; master may store "CY / CY"
            String spaced = input.replace("/", " / ").replaceAll("\\s+", " ").trim();
            if (!spaced.equals(input)) {
                rows = commercialJdbcTemplate.query(sql, (rs, rowNum) -> {
                    String v = rs.getString(1);
                    return v == null ? "" : v.trim();
                }, spaced, spaced);
                if (!rows.isEmpty() && rows.get(0) != null && !rows.get(0).isEmpty()) {
                    return rows.get(0);
                }
            }
            logger.warn("transportservice code not found for name/code='{}'", input);
            return "";
        } catch (DataAccessException e) {
            logger.warn("transportservice lookup failed for '{}': {}", input, e.getMessage());
            return "";
        }
    }
}
