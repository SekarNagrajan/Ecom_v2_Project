package com.svm.ecom.rates.surcharge;

import com.svm.lrp.pnc.common.surcharge.bean.CommonSurchargeBean;
import com.svm.lrp.pnc.common.surcharge.bean.CommonSurchargeLocal;
import com.svm.lrp.pnc.common.surcharge.client.CommonSurcharge;
import com.svm.lrp.pnc.common.surcharge.shared.vo.surVO;
import com.svm.lrp.pnc.common.surcharge.shared.vo.surchargeVO;
import net.ecom.vo.ContractSurchargeCalculationResult;
import net.ecom.vo.ContractSurchargeResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Thin adapter around Booking {@link CommonSurcharge} ({@code AutoRateFlag = "A"}).
 * <p>
 * Mirrors Booking {@code newAutoSurcharge} after {@code autoOFRNew2}:
 * <ol>
 *   <li>Load container surcharges</li>
 *   <li>Inject WGT + contract OFR elements ({@code getOFRByRef})</li>
 *   <li>Collect flat amounts, re-inject for formulas, collect formula/LP</li>
 * </ol>
 * Contract OFR <em>lines</em> stay outside this adapter (merged in {@code RateEstimateService}).
 */
@Component
public class CommonSurchargeEngineAdapter {

    private static final Logger logger = LoggerFactory.getLogger(CommonSurchargeEngineAdapter.class);

    /** Existing entry — surcharge only (no contract OFR seed). */
    public ContractSurchargeCalculationResult calculate(surchargeVO vo) {
        return calculate(vo, null);
    }

    /**
     * @param ofrUnitByEquipmentType optional Booking {@code getOFRByRef} seed:
     *        equipment type (e.g. {@code 20DC}) → unit OFR amount.
     *        Each {@code eqpid} is seeded with its matching eqp type's OFR (not one global OFR).
     *        Does not change surcharge tariff load; only formula elements.
     */
    public ContractSurchargeCalculationResult calculate(surchargeVO vo,
                                                        Map<String, Double> ofrUnitByEquipmentType) {
        ContractSurchargeCalculationResult result = new ContractSurchargeCalculationResult();
        result.setChargesByPairId(new HashMap<String, Map<String, ContractSurchargeResultVo>>());
        if (vo == null) {
            return result;
        }

        // Snapshot BEFORE engine runs: getContainerAmount / HT processing overwrite vo.eqptype
        // with rate units (TEUS/UN/OG). Without this, all BAF rows merge as BAF|TEUS.
        Map<String, String> cargoEqpById = snapshotCargoEqpById(vo);
        List<String> cargoEqpOrder = snapshotCargoEqpOrder(vo);

        try {
            CommonSurcharge cs = createWiredClient();

            // 1) Booking: getSurchargeByContainerBooking(vo) — on this jar: New + process
            invokeSurchargeByContainer(cs, vo);

            // 2) Booking setCAF / getOFRByRef — WGT + per-eqp OFR/FRT
            setCAFComponents(cs, vo);
            seedContractOfrElements(cs, vo, ofrUnitByEquipmentType);

            // 3) Booking: getContainerAmount() → grid, then addElementsByContainer for each charge
            mapAmountSource(cs, vo, result, cargoEqpById, cargoEqpOrder,
                    "getContainerAmount", "cntHTAmt", cs::getContainerAmount);
            injectCollectedChargesAsFormulaElements(cs, result);
            injectOfrFrtElements(cs, result);

            // 4) Booking: getContainerFormula / LP after OFR+charge bases are in the engine
            mapAmountSource(cs, vo, result, cargoEqpById, cargoEqpOrder,
                    "getContainerFormula", "cntHTFor", cs::getContainerFormula);
            mapAmountSource(cs, vo, result, cargoEqpById, cargoEqpOrder,
                    "getLPAmount", "lpHTAmt", cs::getLPAmount);
            mapAmountSource(cs, vo, result, cargoEqpById, cargoEqpOrder,
                    "getLPFormula", "lpHTFor", cs::getLPFormula);

            // Last line of defense: never leave TEUS/UN on response rows when cargo is known
            stampCargoEquipmentTypes(result, cargoEqpById);

            logger.debug("CommonSurcharge engine produced {} pair(s)",
                    result.getChargesByPairId() != null ? result.getChargesByPairId().size() : 0);
        } catch (Exception e) {
            logger.error("CommonSurcharge calculation failed", e);
            throw new IllegalStateException("Surcharge calculation failed: " + e.getMessage(), e);
        }
        return result;
    }

    /** eqpid → cargo equipment type (20DC/…) captured before engine mutates the VO. */
    static Map<String, String> snapshotCargoEqpById(surchargeVO vo) {
        Map<String, String> map = new LinkedHashMap<>();
        if (vo == null || vo.eqpid == null) {
            return map;
        }
        for (int i = 0; i < vo.eqpid.length; i++) {
            if (vo.eqpid[i] == null || vo.eqpid[i].trim().isEmpty()) {
                continue;
            }
            String id = vo.eqpid[i].trim();
            String eqp = "";
            if (vo.eqptype != null && i < vo.eqptype.length && vo.eqptype[i] != null) {
                eqp = vo.eqptype[i].trim();
            }
            if (!eqp.isEmpty() && !isRateUnitNotEquipment(eqp)) {
                map.put(id, eqp);
            }
        }
        return map;
    }

    /**
     * Cargo types in request line order (one entry per eqpid line, including qty duplicates).
     * Used when flat amount rows are TEUS and eqpid alignment is missing — assign by
     * per-chargeCode ordinal (1st BAF→1st cargo, 2nd BAF→2nd cargo, …).
     */
    static List<String> snapshotCargoEqpOrder(surchargeVO vo) {
        List<String> order = new ArrayList<>();
        if (vo == null || vo.eqptype == null) {
            return order;
        }
        for (int i = 0; i < vo.eqptype.length; i++) {
            if (vo.eqptype[i] == null || vo.eqptype[i].trim().isEmpty()) {
                continue;
            }
            String eqp = vo.eqptype[i].trim();
            if (!isRateUnitNotEquipment(eqp) && !"LP".equalsIgnoreCase(eqp)) {
                order.add(eqp);
            }
        }
        return order;
    }

    /**
     * Replace rate-unit equipmentType (TEUS/…) with cargo type for each eqpid bucket.
     * Does not change amounts — only response merge key / display type.
     */
    static void stampCargoEquipmentTypes(ContractSurchargeCalculationResult result,
                                         Map<String, String> cargoEqpById) {
        if (result == null || result.getChargesByPairId() == null
                || cargoEqpById == null || cargoEqpById.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Map<String, ContractSurchargeResultVo>> pair
                : result.getChargesByPairId().entrySet()) {
            if (pair.getKey() == null || pair.getValue() == null) {
                continue;
            }
            String cargo = cargoEqpById.get(pair.getKey().trim());
            if (cargo == null || cargo.isEmpty()) {
                continue;
            }
            for (ContractSurchargeResultVo charge : pair.getValue().values()) {
                if (charge == null) {
                    continue;
                }
                String eqp = charge.getEqpType() == null ? "" : charge.getEqpType().trim();
                if ("LP".equalsIgnoreCase(eqp)) {
                    continue;
                }
                if (eqp.isEmpty() || isRateUnitNotEquipment(eqp)) {
                    charge.setEqpType(cargo);
                }
            }
        }
    }

    /**
     * Booking {@code getOFRByRef}: inject OFR unit amount + {@code FRT} per equipment for % formulas.
     * Only OFR is seeded (freight base). Other O-flag lines (THL/THD/ADD/ADL) stay on the response
     * via merge — seeding them does not change tariff load and can pollute formula elements.
     * <p>
     * Multi-eqp / mixed rate types: each {@code vo.eqptype[i]} gets that equipment's unit OFR
     * (not the first container's OFR applied to all).
     */
    void seedContractOfrElements(CommonSurcharge cs, surchargeVO vo,
                                 Map<String, Double> ofrUnitByEquipmentType) {
        if (cs == null || vo == null || vo.eqpid == null
                || ofrUnitByEquipmentType == null || ofrUnitByEquipmentType.isEmpty()) {
            return;
        }
        int seeded = 0;
        for (int i = 0; i < vo.eqpid.length; i++) {
            String eqpId = vo.eqpid[i];
            if (eqpId == null || eqpId.trim().isEmpty()) {
                continue;
            }
            String eqpType = "";
            if (vo.eqptype != null && i < vo.eqptype.length && vo.eqptype[i] != null) {
                eqpType = vo.eqptype[i].trim();
            }
            Double ofrUnit = resolveOfrUnitForEqp(ofrUnitByEquipmentType, eqpType);
            if (ofrUnit == null || ofrUnit == 0.0d) {
                continue;
            }
            String id = eqpId.trim();
            cs.addElementsByContainer(id, "OFR", ofrUnit);
            cs.addElementsByContainer(id, "FRT", ofrUnit);
            seeded++;
        }
        logger.debug("Seeded contract OFR/FRT elements (getOFRByRef) per-eqp: seeded={}, map={}",
                seeded, ofrUnitByEquipmentType);
    }

    /**
     * Lookup unit OFR for an equipment type (case-insensitive).
     * Single-entry map fallback covers VO missing eqptype on odd paths.
     */
    private static Double resolveOfrUnitForEqp(Map<String, Double> ofrByEqp, String eqpType) {
        if (ofrByEqp == null || ofrByEqp.isEmpty()) {
            return null;
        }
        if (eqpType != null && !eqpType.trim().isEmpty()) {
            Double direct = ofrByEqp.get(eqpType.trim());
            if (direct != null) {
                return direct;
            }
            for (Map.Entry<String, Double> e : ofrByEqp.entrySet()) {
                if (e.getKey() != null && eqpType.equalsIgnoreCase(e.getKey().trim())) {
                    return e.getValue();
                }
            }
            return null;
        }
        if (ofrByEqp.size() == 1) {
            return ofrByEqp.values().iterator().next();
        }
        return null;
    }

    /**
     * Creates {@link CommonSurcharge} without JNDI and injects a local bean proxy
     * into {@code merremote}.
     */
    CommonSurcharge createWiredClient() throws Exception {
        CommonSurcharge cs = new CommonSurcharge();
        CommonSurchargeBean bean = new CommonSurchargeBean();
        try {
            bean.ejbCreate();
        } catch (Exception e) {
            logger.debug("ejbCreate skipped/failed (non-fatal for in-process use): {}", e.toString());
        }

        CommonSurchargeLocal local = (CommonSurchargeLocal) Proxy.newProxyInstance(
                CommonSurchargeLocal.class.getClassLoader(),
                new Class<?>[]{CommonSurchargeLocal.class},
                new BeanInvocationHandler(bean));

        Field merremote = findField(CommonSurcharge.class, "merremote");
        if (merremote == null) {
            throw new IllegalStateException("CommonSurcharge.merremote field not found");
        }
        merremote.setAccessible(true);
        merremote.set(cs, local);
        return cs;
    }

    /**
     * Estimate equivalent of Booking {@code setCAFComponents}.
     * Booking walks USD rows on the charges grid; Estimate injects WGT from the VO.
     */
    void setCAFComponents(CommonSurcharge cs, surchargeVO vo) {
        if (cs == null || vo == null || vo.eqpid == null) {
            return;
        }
        for (int i = 0; i < vo.eqpid.length; i++) {
            String eqpId = vo.eqpid[i];
            if (eqpId == null || eqpId.trim().isEmpty()) {
                continue;
            }
            if (vo.weight != null && i < vo.weight.length) {
                cs.addElementsByContainer(eqpId.trim(), "WGT", vo.weight[i]);
            }
        }
    }

    /**
     * Booking after {@code getContainerAmount}: each USD charge is fed back into the
     * formula engine via {@code addElementsByContainer(eqpId, chargeCode, usd)}.
     */
    void injectCollectedChargesAsFormulaElements(CommonSurcharge cs,
                                                   ContractSurchargeCalculationResult result) {
        if (cs == null || result == null || result.getChargesByPairId() == null) {
            return;
        }
        for (Map.Entry<String, Map<String, ContractSurchargeResultVo>> pair
                : result.getChargesByPairId().entrySet()) {
            String eqpId = pair.getKey();
            if (eqpId == null || eqpId.trim().isEmpty() || pair.getValue() == null) {
                continue;
            }
            for (ContractSurchargeResultVo charge : pair.getValue().values()) {
                if (charge == null || charge.getChargeCode() == null) {
                    continue;
                }
                double usd = parseDouble(charge.getAmountUsd());
                if (usd == 0.0d) {
                    continue;
                }
                cs.addElementsByContainer(eqpId.trim(), charge.getChargeCode().trim(), usd);
            }
        }
    }

    /**
     * Booking {@code getOFRByRef} also injects {@code FRT} (and OFR charge id) per equipment.
     * When an OFR row is already present in flat amounts, mirror that FRT injection so
     * %‑of‑freight formulas can resolve. Full OFR rate-engine lookup is out of this jar.
     */
    void injectOfrFrtElements(CommonSurcharge cs, ContractSurchargeCalculationResult result) {
        if (cs == null || result == null || result.getChargesByPairId() == null) {
            return;
        }
        for (Map.Entry<String, Map<String, ContractSurchargeResultVo>> pair
                : result.getChargesByPairId().entrySet()) {
            String eqpId = pair.getKey();
            Map<String, ContractSurchargeResultVo> charges = pair.getValue();
            if (eqpId == null || eqpId.trim().isEmpty() || charges == null) {
                continue;
            }
            ContractSurchargeResultVo ofr = findChargeIgnoreCase(charges, "OFR");
            if (ofr == null) {
                continue;
            }
            double frtUsd = parseDouble(ofr.getAmountUsd());
            if (frtUsd == 0.0d) {
                continue;
            }
            cs.addElementsByContainer(eqpId.trim(), "OFR", frtUsd);
            cs.addElementsByContainer(eqpId.trim(), "FRT", frtUsd);
            logger.debug("Injected OFR/FRT elements for eqpId={} usd={}", eqpId, frtUsd);
        }
    }

    private ContractSurchargeResultVo findChargeIgnoreCase(
            Map<String, ContractSurchargeResultVo> charges, String code) {
        if (charges == null || code == null) {
            return null;
        }
        ContractSurchargeResultVo direct = charges.get(code);
        if (direct != null) {
            return direct;
        }
        for (Map.Entry<String, ContractSurchargeResultVo> e : charges.entrySet()) {
            if (e.getKey() != null && code.equalsIgnoreCase(e.getKey().trim())) {
                return e.getValue();
            }
        }
        return null;
    }

    private double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0d;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0d;
        }
    }

    /**
     * Booking calls {@code getSurchargeByContainerBooking}. This SurCharges jar does not
     * expose that method; {@code CommonSurchargeBean.getSurchargeByContainer} is a stub
     * that always returns null. Real tariff load is {@code getSurchargeByContainerNew}.
     */
    void invokeSurchargeByContainer(CommonSurcharge cs, surchargeVO vo) throws Exception {
        preloadContainerSurchargeHt(cs, vo);
        // Client skips the remote stub when ht is already set, then processes EQAMT/EQFOR/LP*.
        cs.getSurchargeByContainer(vo);
    }

    /**
     * Loads surcharge rows via {@code getSurchargeByContainerNew} into {@code CommonSurcharge.ht}.
     */
    @SuppressWarnings("rawtypes")
    void preloadContainerSurchargeHt(CommonSurcharge cs, surchargeVO vo) throws Exception {
        Field merremoteField = findField(CommonSurcharge.class, "merremote");
        if (merremoteField == null) {
            throw new IllegalStateException("CommonSurcharge.merremote field not found");
        }
        merremoteField.setAccessible(true);
        Object merremote = merremoteField.get(cs);
        if (!(merremote instanceof CommonSurchargeLocal)) {
            throw new IllegalStateException("CommonSurcharge.merremote is not a CommonSurchargeLocal");
        }

        Hashtable ht = ((CommonSurchargeLocal) merremote).getSurchargeByContainerNew(vo);
        if (ht == null) {
            throw new IllegalStateException(
                    "getSurchargeByContainerNew returned null — cannot calculate surcharges");
        }

        Field htField = findField(CommonSurcharge.class, "ht");
        if (htField == null) {
            throw new IllegalStateException("CommonSurcharge.ht field not found");
        }
        htField.setAccessible(true);
        htField.set(cs, ht);

        Object eqAmt = ht.get("EQAMT");
        Object eqFor = ht.get("EQFOR");
        Object lpAmt = ht.get("LPAMT");
        Object lpFor = ht.get("LPFOR");
        logger.debug(
                "Preloaded CommonSurcharge.ht keys={}, EQAMT={}, EQFOR={}, LPAMT={}, LPFOR={}",
                ht.keySet(),
                sizeOf(eqAmt),
                sizeOf(eqFor),
                sizeOf(lpAmt),
                sizeOf(lpFor));
    }

    /**
     * Tries the Booking client getter first; on linkage/runtime failure (e.g. missing
     * {@code chargetype} field) reads the same {@code surVO} rows the getter would have
     * flattened from {@code cntHTAmt}/{@code cntHTFor}/{@code lpHTAmt}/{@code lpHTFor}.
     * <p>
     * Equivalent to Booking {@code cs.getContainerAmount()} / formula / LP collectors
     * for chargeCode + amountUSD + eqpType — skips only the broken {@code chargetype[]} write.
     * Response equipment type uses cargo {@code vo.eqptype} (Booking grid), not rate unit TEUS/UN.
     */
    private void mapAmountSource(CommonSurcharge cs,
                                 surchargeVO requestVo,
                                 ContractSurchargeCalculationResult result,
                                 Map<String, String> cargoEqpById,
                                 List<String> cargoEqpOrder,
                                 String source,
                                 String htFieldName,
                                 AmountGetter getter) {
        // Invoke getter for Booking side-effects (may flatten into HT); then prefer HT
        // because flat arrays often stamp rate-unit TEUS and collapse BAF on merge.
        surchargeVO amounts = null;
        try {
            amounts = getter.get();
        } catch (Throwable t) {
            logger.warn("{} failed ({}), using internal field {} only",
                    source, t.toString(), htFieldName);
        }

        int before = countCharges(result);
        mapFromInternalHt(cs, htFieldName, result, cargoEqpById);
        if (countCharges(result) > before) {
            return;
        }
        if (amounts != null) {
            mapContainerAmount(amounts, requestVo, result, cargoEqpById, cargoEqpOrder);
        }
    }

    private interface AmountGetter {
        surchargeVO get();
    }

    private static int countCharges(ContractSurchargeCalculationResult result) {
        if (result == null || result.getChargesByPairId() == null) {
            return 0;
        }
        int n = 0;
        for (Map<String, ContractSurchargeResultVo> m : result.getChargesByPairId().values()) {
            if (m != null) {
                n += m.size();
            }
        }
        return n;
    }

    /**
     * Reads {@code eqpId → (chargeKey → surVO)} hashtables populated by
     * {@code getSurchargeByContainer} (cntHTAmt / cntHTFor / lpHTAmt / lpHTFor).
     */
    @SuppressWarnings("rawtypes")
    void mapFromInternalHt(CommonSurcharge cs, String htFieldName,
                           ContractSurchargeCalculationResult result,
                           Map<String, String> cargoEqpById) {
        Field field = findField(CommonSurcharge.class, htFieldName);
        if (field == null || cs == null) {
            return;
        }
        try {
            field.setAccessible(true);
            Object raw = field.get(cs);
            if (!(raw instanceof Hashtable)) {
                return;
            }
            Hashtable outer = (Hashtable) raw;
            for (Object eqpKey : outer.keySet()) {
                String pairId = eqpKey == null ? "" : String.valueOf(eqpKey).trim();
                if (pairId.isEmpty()) {
                    continue;
                }
                Object inner = outer.get(eqpKey);
                if (inner instanceof Hashtable) {
                    for (Object value : ((Hashtable) inner).values()) {
                        addSurVoCharge(pairId, value, cargoEqpById, result);
                    }
                } else {
                    addSurVoCharge(pairId, inner, cargoEqpById, result);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed reading internal {}: {}", htFieldName, e.toString());
        }
    }

    private void addSurVoCharge(String pairId, Object value,
                                Map<String, String> cargoEqpById,
                                ContractSurchargeCalculationResult result) {
        if (!(value instanceof surVO)) {
            return;
        }
        surVO sur = (surVO) value;
        String chargeCode = sur.chargeCode;
        if (chargeCode == null || chargeCode.trim().isEmpty()) {
            return;
        }
        ContractSurchargeResultVo charge = new ContractSurchargeResultVo();
        charge.setChargeCode(chargeCode.trim());
        charge.setAmount(formatAmount(sur.amount));
        charge.setCurrency(firstNonBlank(sur.currencyCode, "USD"));
        charge.setAmountUsd(formatAmount(sur.amountUSD));
        // Booking: EquipmentType = cargo eqptype; TEUS/UN stay BaseRateUnit (not response eqp)
        charge.setEqpType(resolveResponseEqpType(cargoEqpById, pairId, sur.eqpType));
        charge.setVatos(firstNonBlank(sur.vatos, "N"));
        charge.setAmtType("A");
        putCharge(result, pairId, charge);
    }

    void mapContainerAmount(surchargeVO amounts, surchargeVO requestVo,
                            ContractSurchargeCalculationResult result,
                            Map<String, String> cargoEqpById,
                            List<String> cargoEqpOrder) {
        if (amounts == null || amounts.chargecode == null) {
            return;
        }

        // Per chargeCode: how many rate-unit rows assigned to cargo order (BAF→20DC, BAF→20RF, …)
        Map<String, Integer> rateUnitOrdinalByCode = new HashMap<>();
        int len = amounts.chargecode.length;
        for (int i = 0; i < len; i++) {
            String chargeCode = amounts.chargecode[i];
            if (chargeCode == null || chargeCode.trim().isEmpty()) {
                continue;
            }
            String code = chargeCode.trim();
            String pairId = resolvePairId(amounts, requestVo, cargoEqpById, i);
            String engineEqp = amounts.eqptype != null && i < amounts.eqptype.length
                    ? nullToEmpty(amounts.eqptype[i])
                    : "";
            String responseEqp = resolveFlatResponseEqpType(
                    cargoEqpById, cargoEqpOrder, rateUnitOrdinalByCode, code, pairId, engineEqp);
            // Keep each TEUS-row under its cargo eqpid bucket so toResponse cannot merge them
            if (!isBlank(responseEqp) && !isRateUnitNotEquipment(responseEqp)
                    && !"LP".equalsIgnoreCase(responseEqp)
                    && (isBlank(pairId) || isRateUnitNotEquipment(pairId)
                    || cargoEqpFor(cargoEqpById, pairId).isEmpty())) {
                String cargoPair = findEqpIdForCargo(cargoEqpById, responseEqp, result);
                if (!isBlank(cargoPair)) {
                    pairId = cargoPair;
                }
            }

            double local = 0.0d;
            if (amounts.amount != null && i < amounts.amount.length) {
                local = amounts.amount[i];
            }
            double usd = 0.0d;
            if (amounts.amountUSD != null && i < amounts.amountUSD.length) {
                usd = amounts.amountUSD[i];
            } else {
                usd = local;
            }
            String currency = "USD";
            if (amounts.currency != null && i < amounts.currency.length
                    && amounts.currency[i] != null
                    && amounts.currency[i].trim().length() > 0) {
                currency = amounts.currency[i].trim();
            }

            ContractSurchargeResultVo charge = new ContractSurchargeResultVo();
            charge.setChargeCode(code);
            charge.setAmount(formatAmount(local));
            charge.setCurrency(currency);
            charge.setAmountUsd(formatAmount(usd));
            charge.setEqpType(responseEqp);
            charge.setVatos("N");
            charge.setAmtType("A");
            putCharge(result, pairId, charge);
        }
    }

    /**
     * When multiple eqpids share the same cargo type, pick the first pair bucket that does not
     * already hold this charge — used only as pairId remapping for TEUS flat rows.
     */
    private String findEqpIdForCargo(Map<String, String> cargoEqpById, String cargoEqp,
                                     ContractSurchargeCalculationResult result) {
        if (cargoEqpById == null || cargoEqp == null) {
            return "";
        }
        String fallback = "";
        for (Map.Entry<String, String> e : cargoEqpById.entrySet()) {
            if (e.getValue() == null || !cargoEqp.equalsIgnoreCase(e.getValue().trim())) {
                continue;
            }
            if (fallback.isEmpty()) {
                fallback = e.getKey();
            }
            Map<String, Map<String, ContractSurchargeResultVo>> byPair =
                    result != null ? result.getChargesByPairId() : null;
            if (byPair == null || !byPair.containsKey(e.getKey())) {
                return e.getKey();
            }
        }
        return fallback;
    }

    /**
     * Booking grid {@code EquipmentType} = cargo type (20DC/…). Engine often returns rate unit
     * (TEUS/UN/OG) in {@code eqptype} — that must not become the merge key or all BAF rows collapse.
     * {@code LP} is left as-is (shipment lump-sum).
     */
    static String resolveResponseEqpType(Map<String, String> cargoEqpById,
                                         String pairId,
                                         String engineEqp) {
        return resolveFlatResponseEqpType(cargoEqpById, null, null, null, pairId, engineEqp);
    }

    /**
     * Resolve display/merge equipment type for a flat amount row, including per-chargeCode
     * ordinal fallback onto {@code cargoEqpOrder} when engine eqp is TEUS/UN/OG.
     */
    static String resolveFlatResponseEqpType(Map<String, String> cargoEqpById,
                                             List<String> cargoEqpOrder,
                                             Map<String, Integer> rateUnitOrdinalByCode,
                                             String chargeCode,
                                             String pairId,
                                             String engineEqp) {
        String cargo = cargoEqpFor(cargoEqpById, pairId);
        String engine = engineEqp == null ? "" : engineEqp.trim();
        if ("LP".equalsIgnoreCase(engine)) {
            return "LP";
        }
        if (!cargo.isEmpty() && (engine.isEmpty() || isRateUnitNotEquipment(engine))) {
            return cargo;
        }
        if (!engine.isEmpty() && !isRateUnitNotEquipment(engine)) {
            return engine;
        }
        if (!cargo.isEmpty()) {
            return cargo;
        }
        if (isRateUnitNotEquipment(engine) || engine.isEmpty()) {
            String fromOrder = nextCargoFromOrder(cargoEqpOrder, rateUnitOrdinalByCode, chargeCode);
            if (!fromOrder.isEmpty()) {
                return fromOrder;
            }
        }
        return engine;
    }

    private static String nextCargoFromOrder(List<String> cargoEqpOrder,
                                             Map<String, Integer> rateUnitOrdinalByCode,
                                             String chargeCode) {
        if (cargoEqpOrder == null || cargoEqpOrder.isEmpty()
                || rateUnitOrdinalByCode == null || chargeCode == null) {
            return "";
        }
        String key = chargeCode.trim().toUpperCase();
        int ord = rateUnitOrdinalByCode.getOrDefault(key, 0);
        rateUnitOrdinalByCode.put(key, ord + 1);
        if (ord >= 0 && ord < cargoEqpOrder.size()) {
            return cargoEqpOrder.get(ord);
        }
        return "";
    }

    private static String cargoEqpFor(Map<String, String> cargoEqpById, String pairId) {
        if (cargoEqpById == null || cargoEqpById.isEmpty()
                || pairId == null || pairId.trim().isEmpty()) {
            return "";
        }
        String hit = cargoEqpById.get(pairId.trim());
        return hit == null ? "" : hit.trim();
    }

    /** Rate / base-rate units — not Booking cargo equipment codes. */
    static boolean isRateUnitNotEquipment(String eqp) {
        return "TEUS".equalsIgnoreCase(eqp)
                || "TEU".equalsIgnoreCase(eqp)
                || "UN".equalsIgnoreCase(eqp)
                || "OG".equalsIgnoreCase(eqp);
    }

    private void putCharge(ContractSurchargeCalculationResult result, String pairId,
                           ContractSurchargeResultVo charge) {
        Map<String, Map<String, ContractSurchargeResultVo>> byPairId = result.getChargesByPairId();
        if (byPairId == null) {
            byPairId = new HashMap<>();
            result.setChargesByPairId(byPairId);
        }
        String pid = pairId == null ? "" : pairId.trim();
        Map<String, ContractSurchargeResultVo> chargeMap = byPairId.get(pid);
        if (chargeMap == null) {
            chargeMap = new LinkedHashMap<>();
            byPairId.put(pid, chargeMap);
        }
        String code = charge.getChargeCode();
        ContractSurchargeResultVo existing = chargeMap.get(code);
        if (existing == null) {
            chargeMap.put(code, charge);
            return;
        }
        // Prefer cargo equipment over rate-unit TEUS/UN if a later collector has the real type
        if (isRateUnitNotEquipment(nullToEmpty(existing.getEqpType()))
                && !isBlank(charge.getEqpType())
                && !isRateUnitNotEquipment(charge.getEqpType())) {
            existing.setEqpType(charge.getEqpType().trim());
        }
    }

    private String firstNonBlank(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    /**
     * Resolve equipment line id for a flat amount row. Prefer engine eqpid when it still
     * maps to a known cargo type; otherwise align by request VO index (Booking parallel arrays).
     */
    private String resolvePairId(surchargeVO amounts, surchargeVO requestVo,
                                 Map<String, String> cargoEqpById, int index) {
        String fromAmounts = "";
        if (amounts != null && amounts.eqpid != null && index < amounts.eqpid.length
                && amounts.eqpid[index] != null
                && amounts.eqpid[index].trim().length() > 0) {
            fromAmounts = amounts.eqpid[index].trim();
        }
        if (!fromAmounts.isEmpty() && !cargoEqpFor(cargoEqpById, fromAmounts).isEmpty()) {
            return fromAmounts;
        }
        // Amounts eqpid may be garbage after engine; prefer request snapshot index when aligned
        if (requestVo != null && requestVo.eqpid != null && index < requestVo.eqpid.length
                && requestVo.eqpid[index] != null
                && requestVo.eqpid[index].trim().length() > 0) {
            String reqId = requestVo.eqpid[index].trim();
            if (!cargoEqpFor(cargoEqpById, reqId).isEmpty()) {
                return reqId;
            }
        }
        if (!fromAmounts.isEmpty() && !isRateUnitNotEquipment(fromAmounts)) {
            return fromAmounts;
        }
        // 1-based index matching Estimate expandLines eqpid ("1","2","3",…)
        String seq = String.valueOf(index + 1);
        if (!cargoEqpFor(cargoEqpById, seq).isEmpty()) {
            return seq;
        }
        if (requestVo != null && requestVo.eqpid != null && index < requestVo.eqpid.length
                && requestVo.eqpid[index] != null
                && requestVo.eqpid[index].trim().length() > 0) {
            return requestVo.eqpid[index].trim();
        }
        return seq;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String formatAmount(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "0";
        }
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    @SuppressWarnings("rawtypes")
    private int sizeOf(Object value) {
        if (value instanceof Hashtable) {
            return ((Hashtable) value).size();
        }
        if (value instanceof Map) {
            return ((Map) value).size();
        }
        return value == null ? -1 : 0;
    }

    private Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Delegates {@link CommonSurchargeLocal} calls to the in-process bean.
     */
    private static final class BeanInvocationHandler implements InvocationHandler {
        private final CommonSurchargeBean bean;

        private BeanInvocationHandler(CommonSurchargeBean bean) {
            this.bean = bean;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            }
            if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            }
            if ("toString".equals(name)) {
                return "CommonSurchargeLocalProxy(" + bean.getClass().getName() + ")";
            }
            if ("getEJBHome".equals(name) || "getPrimaryKey".equals(name)
                    || "getHandle".equals(name) || "isIdentical".equals(name)
                    || "remove".equals(name)) {
                return null;
            }
            Method target = CommonSurchargeBean.class.getMethod(name, method.getParameterTypes());
            try {
                return target.invoke(bean, args);
            } catch (java.lang.reflect.InvocationTargetException e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                if (cause instanceof Exception) {
                    throw cause;
                }
                if (cause instanceof Error) {
                    throw cause;
                }
                throw e;
            }
        }
    }
}
