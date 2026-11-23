package com.svm.ecom.rates.service;

import com.svm.ecom.rates.dto.estimate.RateEstimateCharge;
import com.svm.ecom.rates.dto.estimate.RateEstimateContainerRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateRequest;
import com.svm.ecom.rates.repository.OfrSrrChargeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Booking Contract OFR engine for Estimate ({@code AutoRateFlag = "O"}).
 * <p>
 * Optimized per-cargo model (Booking {@code getReferenceValues} / {@code autoOFRNew2}):
 * <pre>
 * FOR each container:
 *   key = (rateType, rateRefNo, rateItem, amend, eqp)
 *   O-lines = dispatch(key):
 *     TAR/FMC → bookingrates_tariff(rateno, itemno) + stamp eqp
 *     CMS/TEN → bookingrates(+amend) + related; CMS may rediscover item by eqp
 *     SRR/CRA → srrdetail / corporatedetailN
 *   IF chargeeqptype=LP → amount once, equipmentType=LP   (ADD/ADL — not × qty)
 *   ELSE → amount × quantity, equipmentType=eqp
 *   Record unit OFR per eqp for getOFRByRef seed (A-flag formulas)
 * </pre>
 * Failures return empty — CommonSurcharge ({@code AutoRateFlag = "A"}) is unaffected.
 */
@Service
public class OfrContractChargeService {

    private static final Logger logger = LoggerFactory.getLogger(OfrContractChargeService.class);

    private static final Set<String> KNOWN_RATE_KINDS = new HashSet<>(Arrays.asList(
            "SRR", "CRA", "SER", "TEN", "TAR", "FMC", "CMS", "SCR", "TAF", "SRA"));

    /**
     * Synced into Commercial {@code bookingrates} / {@code bookingrates_tariff} / {@code ratesurcharge}
     * (Booking GetOFRDetailNew path — includes CMS).
     */
    private static final Set<String> COMMERCIAL_SYNCED_KINDS = new HashSet<>(Arrays.asList(
            "TEN", "TAR", "FMC", "CMS", "SCR", "TAF", "SRA"));

    /** Booking date format used in getSRRDetail validfrom/validto binds. */
    private static final DateTimeFormatter BKG_DATE =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private final OfrSrrChargeRepository ofrSrrChargeRepository;
    private final RateEstimateLookupCacheService lookupCache;

    public OfrContractChargeService(OfrSrrChargeRepository ofrSrrChargeRepository,
                                    RateEstimateLookupCacheService lookupCache) {
        this.ofrSrrChargeRepository = ofrSrrChargeRepository;
        this.lookupCache = lookupCache;
    }

    /**
     * Booking {@code autoOFRNew2}: resolve AutoRateFlag=O charges + unit amounts for getOFRByRef.
     * Dedupes DB loads for identical rate keys; applies LP / qty / eqp stamp per container.
     */
    public ContractOfrResult resolve(RateEstimateRequest request) {
        if (request == null || request.getContainers() == null || request.getContainers().isEmpty()) {
            return ContractOfrResult.empty();
        }
        Map<String, RateEstimateCharge> byCode = new LinkedHashMap<>();
        // Booking getOFRByRef: unit OFR per equipment type (not × qty, not first-wins across eqps)
        Map<String, Double> ofrUnitByEqp = new LinkedHashMap<>();
        String pol = trim(request.getPolId());
        String pod = trim(request.getPodId());
        String validityDate = resolveValidityDate(request);
        // Same rate key → reuse unit lines (re-stamp eqp / × qty below)
        Map<String, List<RateEstimateCharge>> unitLinesMemo = new HashMap<>();
        OfrBatchContext batch = preloadOfrBatches(request);

        try {
            for (RateEstimateContainerRequest container : request.getContainers()) {
                if (container == null || isBlank(container.getRateRefNo())) {
                    continue;
                }
                String rateNo = container.getRateRefNo().trim();
                int qty = container.getQuantity() == null || container.getQuantity() < 1
                        ? 1 : container.getQuantity();
                RateKeys keys = resolveKeys(container);
                String eqp = trim(container.getCode());

                logger.debug("Contract OFR (flag=O): kind={}, rateNo={}, item={}, amend={}, eqp={}, qty={}",
                        keys.kind, rateNo, keys.itemNo, keys.amendmentNo, eqp, qty);

                String memoKey = linesMemoKey(keys, rateNo, eqp, pol, pod, validityDate);
                List<RateEstimateCharge> unitLines = unitLinesMemo.get(memoKey);
                if (unitLines == null) {
                    unitLines = loadLines(batch, keys, rateNo, eqp, pol, pod, validityDate);
                    unitLinesMemo.put(memoKey, unitLines);
                }
                if (unitLines.isEmpty()) {
                    logger.debug("Contract OFR 0 rows: kind={}, rateNo={}, item={}, amend={}, eqp={}",
                            keys.kind, rateNo, keys.itemNo, keys.amendmentNo, eqp);
                    continue;
                }
                for (RateEstimateCharge unit : unitLines) {
                    if (unit == null || isBlank(unit.getChargeCode())) {
                        continue;
                    }
                    String code = unit.getChargeCode().trim();
                    // Unit OFR for this eqp — seed formulas; do not overwrite another eqp's OFR
                    if ("OFR".equalsIgnoreCase(code) && !isBlank(eqp) && !ofrUnitByEqp.containsKey(eqp)) {
                        ofrUnitByEqp.put(eqp, parse(unit.getAmount()).doubleValue());
                    }
                    RateEstimateCharge forResponse = copyCharge(unit);
                    // Booking: LP (charges.chargeeqptype) = shipment lump-sum, qty stays 1;
                    // UN/TEU/… = per container → × quantity  (Scenario 2: ADD/ADL NOT × qty)
                    boolean lumpSum = isLumpSumCharge(code);
                    if (qty > 1 && !lumpSum) {
                        multiplyAmount(forResponse, qty);
                    }
                    // Booking checkLPcharges / autoOFRNew2: LP → "LP"; else stamp cargo eqp
                    if (lumpSum) {
                        forResponse.setEquipmentType("LP");
                    } else if (!isBlank(eqp)) {
                        forResponse.setEquipmentType(eqp);
                    }
                    merge(byCode, forResponse, lumpSum);
                }
            }
        } catch (Exception e) {
            logger.warn("Contract OFR resolve failed (surcharge path unchanged): {}", e.toString());
            return ContractOfrResult.empty();
        }
        logger.info("Contract OFR resolved: {} line(s), ofrByEqp={}",
                byCode.size(), ofrUnitByEqp.keySet());
        return new ContractOfrResult(new ArrayList<>(byCode.values()), ofrUnitByEqp);
    }

    /**
     * Preload TAR/FMC and Commercial OFR JDBC for all distinct keys in this request
     * (one/batched query per rateNo group instead of per container).
     */
    private OfrBatchContext preloadOfrBatches(RateEstimateRequest request) {
        Map<String, Set<String>> tarItemsByRate = new LinkedHashMap<>();
        Map<String, Set<String>> commercialItemsByGroup = new LinkedHashMap<>();
        Map<String, String[]> commercialGroupMeta = new HashMap<>();

        for (RateEstimateContainerRequest container : request.getContainers()) {
            if (container == null || isBlank(container.getRateRefNo()) || isBlank(container.getRateItem())) {
                continue;
            }
            RateKeys keys = resolveKeys(container);
            String rateNo = container.getRateRefNo().trim();
            String item = keys.itemNo == null ? "" : keys.itemNo.trim();
            if (item.isEmpty()) {
                continue;
            }
            if ("TAR".equals(keys.kind) || "FMC".equals(keys.kind)) {
                tarItemsByRate.computeIfAbsent(rateNo, k -> new LinkedHashSet<>()).add(item);
            } else if (COMMERCIAL_SYNCED_KINDS.contains(keys.kind)) {
                String amend = isBlank(keys.amendmentNo) ? "0" : keys.amendmentNo.trim();
                String groupKey = rateNo + "|" + keys.kind + "|" + amend;
                commercialItemsByGroup.computeIfAbsent(groupKey, k -> new LinkedHashSet<>()).add(item);
                commercialGroupMeta.put(groupKey, new String[]{rateNo, keys.kind, amend});
            }
        }

        Map<String, List<Map<String, Object>>> tarOfr = new HashMap<>();
        Map<String, List<Map<String, Object>>> rateSurcharge = new HashMap<>();
        Map<String, List<Map<String, Object>>> bookingRates = new HashMap<>();

        for (Map.Entry<String, Set<String>> e : tarItemsByRate.entrySet()) {
            String rateNo = e.getKey();
            Map<String, List<Map<String, Object>>> ofrByItem =
                    ofrSrrChargeRepository.findTarOfrByItems(rateNo, e.getValue());
            Map<String, List<Map<String, Object>>> relatedByItem =
                    ofrSrrChargeRepository.findRateSurchargesByItems(rateNo, e.getValue());
            for (String item : e.getValue()) {
                String k = rateItemKey(rateNo, item);
                tarOfr.put(k, ofrByItem.getOrDefault(item, Collections.emptyList()));
                rateSurcharge.put(k, relatedByItem.getOrDefault(item, Collections.emptyList()));
            }
            logger.debug("OFR TAR batch preload: rateNo={}, items={}", rateNo, e.getValue().size());
        }

        for (Map.Entry<String, Set<String>> e : commercialItemsByGroup.entrySet()) {
            String[] meta = commercialGroupMeta.get(e.getKey());
            if (meta == null) {
                continue;
            }
            String rateNo = meta[0];
            String rateType = meta[1];
            String amend = meta[2];
            Map<String, List<Map<String, Object>>> ofrByItem =
                    ofrSrrChargeRepository.findBookingRatesOfrByItems(
                            rateNo, rateType, amend, e.getValue());
            Set<String> missingRelated = new LinkedHashSet<>();
            for (String item : e.getValue()) {
                if (!rateSurcharge.containsKey(rateItemKey(rateNo, item))) {
                    missingRelated.add(item);
                }
            }
            if (!missingRelated.isEmpty()) {
                Map<String, List<Map<String, Object>>> relatedByItem =
                        ofrSrrChargeRepository.findRateSurchargesByItems(rateNo, missingRelated);
                for (String item : missingRelated) {
                    rateSurcharge.put(rateItemKey(rateNo, item),
                            relatedByItem.getOrDefault(item, Collections.emptyList()));
                }
            }
            for (String item : e.getValue()) {
                bookingRates.put(bookingRatesKey(rateNo, item, rateType, amend),
                        ofrByItem.getOrDefault(item, Collections.emptyList()));
            }
            logger.debug("OFR commercial batch preload: rateNo={}, type={}, amend={}, items={}",
                    rateNo, rateType, amend, e.getValue().size());
        }

        return new OfrBatchContext(tarOfr, rateSurcharge, bookingRates);
    }

    private static String rateItemKey(String rateNo, String itemNo) {
        return rateNo + "|" + itemNo;
    }

    private static String bookingRatesKey(String rateNo, String itemNo, String rateType, String amend) {
        return rateNo + "|" + itemNo + "|" + rateType + "|" + amend;
    }

    /**
     * Memo key for unit-line JDBC result. TAR/FMC omit eqp (SQL is item-only; stamp later).
     * Commercial / SRR / CRA include eqp where row filter or discovery depends on it.
     */
    private String linesMemoKey(RateKeys keys, String rateNo, String eqp,
                                String pol, String pod, String validityDate) {
        if ("TAR".equals(keys.kind) || "FMC".equals(keys.kind)) {
            return keys.kind + "|" + rateNo + "|" + nullToEmpty(keys.itemNo);
        }
        if (COMMERCIAL_SYNCED_KINDS.contains(keys.kind)) {
            return keys.kind + "|" + rateNo + "|" + nullToEmpty(keys.itemNo) + "|"
                    + nullToEmpty(keys.amendmentNo) + "|" + nullToEmpty(eqp);
        }
        if ("CRA".equals(keys.kind) || "SER".equals(keys.kind)) {
            return keys.kind + "|" + rateNo + "|" + nullToEmpty(keys.itemNo) + "|" + nullToEmpty(eqp);
        }
        return "SRR|" + rateNo + "|" + nullToEmpty(keys.itemNo) + "|" + nullToEmpty(eqp) + "|"
                + nullToEmpty(pol) + "|" + nullToEmpty(pod) + "|" + nullToEmpty(validityDate);
    }

    private static String nullToEmpty(String v) {
        return v == null ? "" : v.trim();
    }

    private RateEstimateCharge copyCharge(RateEstimateCharge source) {
        RateEstimateCharge c = new RateEstimateCharge();
        c.setChargeCode(source.getChargeCode());
        c.setChargeName(source.getChargeName());
        c.setAmount(source.getAmount());
        c.setCurrency(source.getCurrency());
        c.setAmountUSD(source.getAmountUSD());
        c.setEquipmentType(source.getEquipmentType());
        return c;
    }

    private List<RateEstimateCharge> loadLines(OfrBatchContext batch, RateKeys keys, String rateNo,
                                               String eqp, String pol, String pod, String validityDate) {
        List<RateEstimateCharge> lines = loadByKind(batch, keys, rateNo, eqp, pol, pod, validityDate);
        if (!lines.isEmpty()) {
            return lines;
        }
        for (String alt : fallbackKinds(keys.kind)) {
            RateKeys altKeys = new RateKeys(alt, keys.itemNo, keys.amendmentNo);
            lines = loadByKind(batch, altKeys, rateNo, eqp, pol, pod, validityDate);
            if (!lines.isEmpty()) {
                logger.debug("Contract OFR source fallback: requested={} → actual={} rateNo={}",
                        keys.kind, alt, rateNo);
                return lines;
            }
        }
        return lines;
    }

    private List<RateEstimateCharge> loadByKind(OfrBatchContext batch, RateKeys keys, String rateNo,
                                                String eqp, String pol, String pod, String validityDate) {
        if ("TAR".equals(keys.kind) || "FMC".equals(keys.kind)) {
            // Booking getOFRDetail TAR/FMC: bookingrates_tariff by rateno+itemno only
            return loadTariffOfrLines(batch, rateNo, keys.itemNo, eqp);
        }
        if (COMMERCIAL_SYNCED_KINDS.contains(keys.kind)) {
            return loadCommercialSyncedLines(batch, rateNo, keys.kind, keys.itemNo, keys.amendmentNo, eqp,
                    pol, pod, validityDate);
        }
        if ("CRA".equals(keys.kind) || "SER".equals(keys.kind)) {
            return loadCraLines(rateNo, keys.itemNo, eqp);
        }
        return loadSrrLines(rateNo, keys.itemNo, eqp, pol, pod, validityDate);
    }

    /**
     * Booking {@code getOFRDetail} TAR/FMC branch:
     * {@code bookingrates_tariff where rateno=? and itemno=?}, then stamp cargo eqp.
     * No eqptype SQL filter — each cargo already carries its own itemno (orderNo).
     */
    private List<RateEstimateCharge> loadTariffOfrLines(OfrBatchContext batch, String rateNo,
                                                        String itemNo, String eqp) {
        if (isBlank(itemNo)) {
            return new ArrayList<>();
        }
        String item = itemNo.trim();
        List<Map<String, Object>> ofrRows = batch.tarOfr(rateNo, item);
        if (ofrRows == null) {
            ofrRows = ofrSrrChargeRepository.findTarOfr(rateNo, item);
            if (ofrRows.isEmpty()) {
                ofrRows = ofrSrrChargeRepository.findTarOfrMinimal(rateNo, item);
            }
        }
        if (ofrRows.isEmpty()) {
            logger.debug("TAR/FMC OFR 0 rows: rateNo={}, item={}", rateNo, itemNo);
            return new ArrayList<>();
        }

        List<RateEstimateCharge> out = new ArrayList<>();
        for (Map<String, Object> row : ofrRows) {
            out.add(toCharge(row, eqp));
        }
        // Related (when present): same as TEN — ratesurcharge vatos='N'
        for (Map<String, Object> row : lookupRateSurcharges(batch, rateNo, item)) {
            RateEstimateCharge charge = toCharge(row, eqp);
            if (isBlank(charge.getChargeCode()) || "OFR".equalsIgnoreCase(charge.getChargeCode())) {
                continue;
            }
            out.add(charge);
        }
        logger.debug("TAR/FMC OFR: rateNo={}, item={}, eqp={}, lines={}",
                rateNo, itemNo, eqp, out.size());
        return out;
    }

    /** Cross-table fallbacks only among Commercial non-synced and SRR/CRA — never CMS→SRR. */
    private String[] fallbackKinds(String primary) {
        if (COMMERCIAL_SYNCED_KINDS.contains(primary)) {
            return new String[0];
        }
        if ("SRR".equals(primary)) {
            return new String[]{"CRA"};
        }
        if ("CRA".equals(primary) || "SER".equals(primary)) {
            return new String[]{"SRR"};
        }
        return new String[0];
    }

    /**
     * Booking-aligned Commercial OFR for TEN/CMS/SCR/TAF/SRA (not TAR/FMC — see {@link #loadTariffOfrLines}):
     * <ol>
     *   <li>{@code bookingrates} by rateno + itemno + ratetype + amendmentno</li>
     *   <li>else {@code bookingrates_tariff} by same keys</li>
     *   <li>CMS: keep rows matching cargo eqp; rediscover itemno from {@code svcratedetail} if needed</li>
     *   <li>Related: {@code ratesurcharge} vatos='N'; CMS family → {@code svcratesurcharge}</li>
     * </ol>
     */
    private List<RateEstimateCharge> loadCommercialSyncedLines(OfrBatchContext batch,
                                                               String rateNo, String rateType,
                                                               String itemNo, String amendmentNo,
                                                               String eqp,
                                                               String pol, String pod,
                                                               String validityDate) {
        if (isBlank(itemNo)) {
            return new ArrayList<>();
        }
        String amend = isBlank(amendmentNo) ? "0" : amendmentNo.trim();
        String resolvedItem = itemNo.trim();

        List<Map<String, Object>> ofrRows =
                filterRowsByEqp(loadCommercialOfrRows(batch, rateNo, rateType, resolvedItem, amend), eqp);

        // bookingrates hit wrong eqp → still try tariff (keys), then CMS item rediscovery
        if (ofrRows.isEmpty()) {
            List<Map<String, Object>> tariff =
                    filterRowsByEqp(
                            ofrSrrChargeRepository.findTariffOfrByKeys(rateNo, resolvedItem, rateType, amend),
                            eqp);
            if (!tariff.isEmpty()) {
                ofrRows = tariff;
            }
        }

        if (ofrRows.isEmpty() && isCmsFamily(rateType) && !isBlank(eqp)) {
            int amendInt = parseAmendment(amend);
            List<String> candidates = ofrSrrChargeRepository.findCmsItemNos(
                    rateNo, amendInt, blankToNull(pol), blankToNull(pod), blankToNull(validityDate), eqp);
            for (String candidate : candidates) {
                if (isBlank(candidate) || candidate.equalsIgnoreCase(resolvedItem)) {
                    continue;
                }
                List<Map<String, Object>> discovered =
                        filterRowsByEqp(
                                loadCommercialOfrRows(batch, rateNo, rateType, candidate, amend), eqp);
                if (!discovered.isEmpty()) {
                    logger.debug("Commercial OFR item rediscovered for eqp={}: {} → {}",
                            eqp, resolvedItem, candidate);
                    resolvedItem = candidate;
                    ofrRows = discovered;
                    break;
                }
            }
        }
        if (ofrRows.isEmpty()) {
            return new ArrayList<>();
        }

        List<RateEstimateCharge> out = new ArrayList<>();
        for (Map<String, Object> row : ofrRows) {
            out.add(toCharge(row, eqp));
        }

        List<Map<String, Object>> related = lookupRateSurcharges(batch, rateNo, resolvedItem);
        if (related.isEmpty() && isCmsFamily(rateType)) {
            int amendInt = parseAmendment(amend);
            related = ofrSrrChargeRepository.findCmsRelated(rateNo, amendInt, resolvedItem);
            logger.debug("Related from contract (vatos≠N): type={}, rateNo={}, item={}, amend={}, rows={}",
                    rateType, rateNo, resolvedItem, amendInt, related.size());
        } else {
            logger.debug("Related from ratesurcharge (vatos=N): rateNo={}, item={}, rows={}",
                    rateNo, resolvedItem, related.size());
        }
        for (Map<String, Object> row : related) {
            RateEstimateCharge charge = toCharge(row, eqp);
            if (isBlank(charge.getChargeCode()) || "OFR".equalsIgnoreCase(charge.getChargeCode())) {
                continue;
            }
            out.add(charge);
        }
        logger.debug("Commercial synced OFR: type={}, rateNo={}, item={}, amend={}, eqp={}, lines={}",
                rateType, rateNo, resolvedItem, amend, eqp, out.size());
        return out;
    }

    private List<Map<String, Object>> loadCommercialOfrRows(OfrBatchContext batch, String rateNo,
                                                            String rateType, String itemNo,
                                                            String amend) {
        List<Map<String, Object>> ofrRows = batch.bookingRates(rateNo, itemNo, rateType, amend);
        if (ofrRows == null) {
            ofrRows = ofrSrrChargeRepository.findBookingRatesOfr(rateNo, itemNo, rateType, amend);
        }
        if (ofrRows.isEmpty()) {
            ofrRows = ofrSrrChargeRepository.findTariffOfrByKeys(rateNo, itemNo, rateType, amend);
        }
        return ofrRows;
    }

    private List<Map<String, Object>> lookupRateSurcharges(OfrBatchContext batch, String rateNo,
                                                           String itemNo) {
        List<Map<String, Object>> related = batch.rateSurcharge(rateNo, itemNo);
        if (related != null) {
            return related;
        }
        return ofrSrrChargeRepository.findRateSurcharges(rateNo, itemNo);
    }

    /**
     * Booking rate key includes eqptype — never attach another equipment's OFR to this cargo.
     * Rows with blank eqptype are kept (tariff / schema variants).
     */
    private List<Map<String, Object>> filterRowsByEqp(List<Map<String, Object>> rows, String eqp) {
        if (rows == null || rows.isEmpty() || isBlank(eqp)) {
            return rows == null ? new ArrayList<>() : rows;
        }
        List<Map<String, Object>> matched = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            String rowEqp = str(row.get("eqptype"));
            if (isBlank(rowEqp) || eqp.equalsIgnoreCase(rowEqp)) {
                matched.add(row);
            }
        }
        return matched;
    }

    private boolean isCmsFamily(String rateType) {
        return "CMS".equals(rateType) || "SCR".equals(rateType)
                || "TAF".equals(rateType) || "SRA".equals(rateType);
    }

    private int parseAmendment(String raw) {
        if (isBlank(raw)) {
            return 0;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Prefer Booking {@code getSRRDetail} (ports + date + eqp + ord), then fall back to
     * {@code getOFRDetail} (srr + ord only), then resolve ord from DB for matching eqp.
     * Related THL/THD/ADD/ADL come from {@code srrdetail} (not ratesurcharge) for SRR.
     */
    private List<RateEstimateCharge> loadSrrLines(String srrNo, String ordNo, String eqpType,
                                                  String pol, String pod, String validityDate) {
        // 1) Port/date path (matches Booking getSRRDetail — related quotestatus='I')
        if (!isBlank(pol) && !isBlank(pod) && !isBlank(validityDate) && !isBlank(ordNo) && !isBlank(eqpType)) {
            List<RateEstimateCharge> byPorts = loadSrrByPorts(srrNo, pol, pod, validityDate, eqpType, ordNo);
            if (!byPorts.isEmpty()) {
                logger.debug("SRR OFR via ports/date: {} line(s)", byPorts.size());
                return byPorts;
            }
        }

        // 2) Simple srr+ord path (getOFRDetail — related via counterby/counterno)
        if (!isBlank(ordNo)) {
            List<RateEstimateCharge> byOrd = loadSrrByOrd(srrNo, ordNo, eqpType);
            if (!byOrd.isEmpty()) {
                logger.debug("SRR OFR via srr+ord: {} line(s)", byOrd.size());
                return byOrd;
            }
        }

        // 3) Discover ordNo from srr+ports+date for this eqp
        if (!isBlank(pol) && !isBlank(pod) && !isBlank(validityDate) && !isBlank(eqpType)) {
            List<Map<String, Object>> eqpOrds =
                    ofrSrrChargeRepository.findSrrEqpOrd(srrNo, pol, pod, validityDate);
            logger.debug("SRR ord discovery (ports): srrNo={}, candidates={}", srrNo, eqpOrds.size());
            for (Map<String, Object> row : eqpOrds) {
                String rowEqp = str(row.get("eqptype"));
                String rowOrd = str(row.get("ordNo"));
                if (!isBlank(eqpType) && !eqpType.equalsIgnoreCase(rowEqp)) {
                    continue;
                }
                if (isBlank(rowOrd)) {
                    continue;
                }
                List<RateEstimateCharge> found =
                        loadSrrByPorts(srrNo, pol, pod, validityDate, rowEqp, rowOrd);
                if (found.isEmpty()) {
                    found = loadSrrByOrd(srrNo, rowOrd, eqpType);
                }
                if (!found.isEmpty()) {
                    logger.debug("SRR OFR via discovered ordNo={} eqp={}: {} line(s)",
                            rowOrd, rowEqp, found.size());
                    return found;
                }
            }
        }

        // 4) Discover OFR rows by srrno only (wrong amendmentNumber / ports / date)
        List<RateEstimateCharge> bySrrOnly = loadSrrByRateNoOnly(srrNo, eqpType);
        if (!bySrrOnly.isEmpty()) {
            logger.debug("SRR OFR via srrno-only discovery: {} line(s)", bySrrOnly.size());
            return bySrrOnly;
        }

        return new ArrayList<>();
    }

    /** Last-resort SRR: OFR + counter-related charges without requiring request ord/ports. */
    private List<RateEstimateCharge> loadSrrByRateNoOnly(String srrNo, String eqpType) {
        List<Map<String, Object>> ofrRows =
                ofrSrrChargeRepository.findSrrOfrBySrrNo(srrNo, blankToNull(eqpType));
        if (ofrRows.isEmpty() && !isBlank(eqpType)) {
            ofrRows = ofrSrrChargeRepository.findSrrOfrBySrrNo(srrNo, null);
        }
        if (ofrRows.isEmpty()) {
            return new ArrayList<>();
        }
        List<RateEstimateCharge> out = new ArrayList<>();
        Map<String, List<Map<String, Object>>> byOrd = new LinkedHashMap<>();
        for (Map<String, Object> row : ofrRows) {
            String ord = str(row.get("ordNo"));
            if (isBlank(ord)) {
                ord = str(row.get("ordno"));
            }
            if (isBlank(ord)) {
                ord = "_";
            }
            byOrd.computeIfAbsent(ord, k -> new ArrayList<>()).add(row);
        }
        for (Map.Entry<String, List<Map<String, Object>>> e : byOrd.entrySet()) {
            String counterBy = "A";
            int counterNo = 0;
            for (Map<String, Object> row : e.getValue()) {
                out.add(toCharge(row, eqpType));
                Counter c = readCounter(row);
                counterBy = c.counterBy;
                counterNo = c.counterNo;
            }
            if ("_".equals(e.getKey())) {
                continue;
            }
            for (Map<String, Object> row
                    : ofrSrrChargeRepository.findSrrRelatedByCounter(
                            srrNo, e.getKey(), counterBy, counterNo)) {
                out.add(toCharge(row, eqpType));
            }
        }
        return out;
    }

    private List<RateEstimateCharge> loadSrrByPorts(String srrNo, String pol, String pod,
                                                    String validityDate, String eqpType, String ordNo) {
        List<RateEstimateCharge> out = new ArrayList<>();
        List<Map<String, Object>> ofrRows =
                ofrSrrChargeRepository.findSrrOfrByPorts(srrNo, pol, pod, validityDate, eqpType, ordNo);
        if (ofrRows.isEmpty()) {
            return out;
        }
        String counterBy = "A";
        int counterNo = 0;
        for (Map<String, Object> row : ofrRows) {
            out.add(toCharge(row, eqpType));
            Counter c = readCounter(row);
            counterBy = c.counterBy;
            counterNo = c.counterNo;
        }
        for (Map<String, Object> row : ofrSrrChargeRepository.findSrrRelatedByPorts(
                srrNo, pol, pod, validityDate, eqpType, ordNo, counterNo, counterBy)) {
            out.add(toCharge(row, eqpType));
        }
        return out;
    }

    private List<RateEstimateCharge> loadSrrByOrd(String srrNo, String ordNo, String eqpType) {
        List<RateEstimateCharge> out = new ArrayList<>();
        List<Map<String, Object>> ofrRows = ofrSrrChargeRepository.findSrrOfrByOrd(srrNo, ordNo);
        if (ofrRows.isEmpty()) {
            return out;
        }
        String counterBy = "A";
        int counterNo = 0;
        for (Map<String, Object> row : ofrRows) {
            String rowEqp = str(row.get("eqptype"));
            // If caller passed eqp, keep matching rows only; else keep all
            if (!isBlank(eqpType) && !isBlank(rowEqp) && !eqpType.equalsIgnoreCase(rowEqp)) {
                continue;
            }
            out.add(toCharge(row, eqpType));
            Counter c = readCounter(row);
            counterBy = c.counterBy;
            counterNo = c.counterNo;
        }
        // No wrong-eqp fallback: empty here lets loadSrrLines discover ord for this eqp (Booking-like)
        if (out.isEmpty()) {
            return out;
        }
        for (Map<String, Object> row
                : ofrSrrChargeRepository.findSrrRelatedByCounter(srrNo, ordNo, counterBy, counterNo)) {
            String rowEqp = str(row.get("eqptype"));
            if (!isBlank(eqpType) && !isBlank(rowEqp) && !eqpType.equalsIgnoreCase(rowEqp)) {
                continue;
            }
            out.add(toCharge(row, eqpType));
        }
        return out;
    }

    private List<RateEstimateCharge> loadCraLines(String corporateNo, String itemNo, String eqpType) {
        List<RateEstimateCharge> out = new ArrayList<>();
        if (isBlank(itemNo)) {
            return out;
        }
        List<Map<String, Object>> ofrRows = ofrSrrChargeRepository.findCraOfr(corporateNo, itemNo);
        if (ofrRows.isEmpty()) {
            return out;
        }
        for (Map<String, Object> row : ofrRows) {
            out.add(toCharge(row, eqpType));
        }
        for (Map<String, Object> row : ofrSrrChargeRepository.findCraRelated(corporateNo, itemNo)) {
            out.add(toCharge(row, eqpType));
        }
        return out;
    }

    /**
     * Booking cargo keys (all required on container):
     * rateRefNo, rateType, rateItem (=itemno), amendmentNumber (=amendmentno).
     */
    private RateKeys resolveKeys(RateEstimateContainerRequest container) {
        String rateType = trim(container.getRateType());
        String itemNo = trim(container.getRateItem());
        String amendNo = trim(container.getAmendmentNumber());
        if (isBlank(amendNo)) {
            amendNo = "0";
        }

        if (isBlank(rateType)) {
            return new RateKeys("SRR", itemNo, amendNo);
        }
        String upper = rateType.toUpperCase(Locale.ROOT);
        if (KNOWN_RATE_KINDS.contains(upper)) {
            return new RateKeys(upper, itemNo, amendNo);
        }
        // Unknown rateType string kept as free-text SRR ord (legacy)
        return new RateKeys("SRR", isBlank(itemNo) ? rateType : itemNo, amendNo);
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private String resolveValidityDate(RateEstimateRequest request) {
        OffsetDateTime dt = request.getCargoReadyDate() != null
                ? request.getCargoReadyDate()
                : request.getEtd();
        if (dt == null) {
            return "";
        }
        return BKG_DATE.format(dt);
    }

    private Counter readCounter(Map<String, Object> row) {
        String counterBy = row.get("counterby") != null
                ? String.valueOf(row.get("counterby")).trim() : "A";
        int counterNo = 0;
        Object cn = row.get("counterno");
        if (cn instanceof Number) {
            counterNo = ((Number) cn).intValue();
        } else if (cn != null) {
            try {
                counterNo = Integer.parseInt(cn.toString().trim());
            } catch (NumberFormatException ignored) {
                // keep 0
            }
        }
        return new Counter(counterBy, counterNo);
    }

    private RateEstimateCharge toCharge(Map<String, Object> row, String fallbackEqp) {
        RateEstimateCharge charge = new RateEstimateCharge();
        String code = str(row.get("charge"));
        charge.setChargeCode(code);
        String name = str(row.get("chargename"));
        charge.setChargeName(lookupCache.resolveChargeName(code, name));
        String currency = str(row.get("currency"));
        charge.setCurrency(isBlank(currency) ? "USD" : currency);
        String amt = str(row.get("usdamt"));
        charge.setAmount(isBlank(amt) ? "0" : amt.trim());
        // Booking OFR grid shows local currency amount; USD parity needs ROE (not in this step)
        charge.setAmountUSD(charge.getAmount());
        String eqp = str(row.get("eqptype"));
        charge.setEquipmentType(isBlank(eqp) ? (fallbackEqp == null ? "" : fallbackEqp) : eqp);
        return charge;
    }

    private void multiplyAmount(RateEstimateCharge charge, int qty) {
        if (qty <= 1) {
            return;
        }
        charge.setAmount(scale(parse(charge.getAmount()).multiply(BigDecimal.valueOf(qty))));
        charge.setAmountUSD(scale(parse(charge.getAmountUSD()).multiply(BigDecimal.valueOf(qty))));
    }

    private void merge(Map<String, RateEstimateCharge> byCode, RateEstimateCharge source, boolean lumpSum) {
        if (source == null || isBlank(source.getChargeCode())) {
            return;
        }
        String code = source.getChargeCode().trim();
        // Booking Auto Rate: one row per chargeCode+equipment (UN); LP = chargeCode only
        String key = lumpSum ? code : chargeKey(code, source.getEquipmentType());
        RateEstimateCharge existing = byCode.get(key);
        if (existing == null) {
            byCode.put(key, source);
            return;
        }
        // Booking checkLPcharges: LP stays once — do not sum across containers
        if (lumpSum) {
            return;
        }
        existing.setAmount(scale(parse(existing.getAmount()).add(parse(source.getAmount()))));
        existing.setAmountUSD(scale(parse(existing.getAmountUSD()).add(parse(source.getAmountUSD()))));
        if (isBlank(existing.getChargeName()) || "-".equals(existing.getChargeName())) {
            existing.setChargeName(source.getChargeName());
        }
        if (isBlank(existing.getCurrency())) {
            existing.setCurrency(source.getCurrency());
        }
        if (isBlank(existing.getEquipmentType()) && !isBlank(source.getEquipmentType())) {
            existing.setEquipmentType(source.getEquipmentType());
        }
    }

    /** Non-LP: {@code chargeCode|equipmentType}; blank eqp → {@code chargeCode} only. */
    private static String chargeKey(String code, String equipmentType) {
        if (equipmentType == null || equipmentType.trim().isEmpty()) {
            return code;
        }
        return code + "|" + equipmentType.trim();
    }

    /**
     * Booking {@code charges.chargeeqptype = 'LP'} → lump-sum / shipment (BaseRateUnit LP, qty 1).
     * Uses shared {@link RateEstimateLookupCacheService} (no per-request DB hit).
     */
    private boolean isLumpSumCharge(String chargeCode) {
        if (isBlank(chargeCode)) {
            return false;
        }
        return lookupCache.isLumpSumCharge(chargeCode);
    }

    private BigDecimal parse(String value) {
        if (isBlank(value)) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private String scale(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private String str(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static final class RateKeys {
        private final String kind;
        private final String itemNo;
        private final String amendmentNo;

        private RateKeys(String kind, String itemNo, String amendmentNo) {
            this.kind = kind;
            this.itemNo = itemNo;
            this.amendmentNo = amendmentNo;
        }
    }

    /**
     * Request-local JDBC preload maps (not stored on the Spring singleton).
     * {@code null} from helpers means "not preloaded — use single-item repository call".
     */
    private static final class OfrBatchContext {
        private final Map<String, List<Map<String, Object>>> tarOfrByRateItem;
        private final Map<String, List<Map<String, Object>>> rateSurchargeByRateItem;
        private final Map<String, List<Map<String, Object>>> bookingRatesByKey;

        private OfrBatchContext(Map<String, List<Map<String, Object>>> tarOfrByRateItem,
                                Map<String, List<Map<String, Object>>> rateSurchargeByRateItem,
                                Map<String, List<Map<String, Object>>> bookingRatesByKey) {
            this.tarOfrByRateItem = tarOfrByRateItem != null ? tarOfrByRateItem : Collections.emptyMap();
            this.rateSurchargeByRateItem =
                    rateSurchargeByRateItem != null ? rateSurchargeByRateItem : Collections.emptyMap();
            this.bookingRatesByKey = bookingRatesByKey != null ? bookingRatesByKey : Collections.emptyMap();
        }

        private List<Map<String, Object>> tarOfr(String rateNo, String itemNo) {
            if (tarOfrByRateItem.isEmpty()) {
                return null;
            }
            return tarOfrByRateItem.get(rateItemKey(rateNo, itemNo));
        }

        private List<Map<String, Object>> rateSurcharge(String rateNo, String itemNo) {
            if (rateSurchargeByRateItem.isEmpty()) {
                return null;
            }
            return rateSurchargeByRateItem.get(rateItemKey(rateNo, itemNo));
        }

        private List<Map<String, Object>> bookingRates(String rateNo, String itemNo,
                                                       String rateType, String amend) {
            if (bookingRatesByKey.isEmpty()) {
                return null;
            }
            return bookingRatesByKey.get(bookingRatesKey(rateNo, itemNo, rateType, amend));
        }
    }

    private static final class Counter {
        private final String counterBy;
        private final int counterNo;

        private Counter(String counterBy, int counterNo) {
            this.counterBy = counterBy;
            this.counterNo = counterNo;
        }
    }
}
