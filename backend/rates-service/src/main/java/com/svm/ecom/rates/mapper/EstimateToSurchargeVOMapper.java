package com.svm.ecom.rates.mapper;

import com.svm.ecom.common.enums.CarriageType;
import com.svm.ecom.common.enums.EquipmentType;
import com.svm.ecom.rates.dto.estimate.RateEstimateCommodityRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateContainerRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateDangerousGoodsRequest;
import com.svm.ecom.rates.dto.estimate.RateEstimateRequest;
import com.svm.ecom.rates.repository.RouteDetailRepository.RouteDetail;
import com.svm.ecom.rates.service.RateEstimateLookupCacheService;
import com.svm.lrp.pnc.common.surcharge.shared.vo.surchargeVO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Maps {@link RateEstimateRequest} to Booking Auto Rate {@link surchargeVO}.
 * Field population mirrors {@code BKGCtrl.newAutoSurcharge()} for values available
 * on the Estimate API; route master gaps are filled from ReportDB {@code Routedetail}.
 */
@Component
public class EstimateToSurchargeVOMapper {

    private static final DateTimeFormatter SURCHARGE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    private static final String EMPTY = "";
    private static final String YES = "Y";
    private static final String NO = "N";
    private static final String CONTAINER_FLAG = "Y";
    private static final String BOOK_NO_ESTIMATE = "ESTIMATE";

    private final RateEstimateLookupCacheService lookupCache;

    public EstimateToSurchargeVOMapper(RateEstimateLookupCacheService lookupCache) {
        this.lookupCache = lookupCache;
    }

    /**
     * Builds a Booking-compatible {@link surchargeVO} from the estimate request
     * and optional {@link RouteDetail} (loaded by {@code routeId}).
     */
    public surchargeVO toSurchargeVO(RateEstimateRequest request, RouteDetail route) {
        if (request == null || request.getContainers() == null || request.getContainers().isEmpty()) {
            return new surchargeVO(0);
        }

        List<ContainerLine> lines = expandLines(request);
        int totcnt = lines.size();
        surchargeVO vo = new surchargeVO(totcnt);

        // --- Booking header fields (newAutoSurcharge) ---
        vo.book_no = "";
        // Prefer request ports; fall back to Routedetail when blank (same as Booking header from route)
        vo.loadport = firstNonEmpty(text(request.getPolId()), routeValue(route, RouteField.POL));
        vo.dischport = firstNonEmpty(text(request.getPodId()), routeValue(route, RouteField.POD));
        vo.originport = firstNonEmpty(text(request.getOriginId()), routeValue(route, RouteField.ORIGIN));
        vo.deliveryport = firstNonEmpty(text(request.getDeliveryId()), routeValue(route, RouteField.DELIVERY));

        // Booking defaults when BMS plugin is off
        vo.ometrocode = EMPTY;
        vo.ometrotype = EMPTY;
        vo.dmetrocode = EMPTY;
        vo.dmetrotype = EMPTY;
        vo.tpla = EMPTY;
        vo.tpremode = EMPTY;

        vo.containflag = CONTAINER_FLAG;

        String rateDate = resolveRateDate(request);
        vo.date = rateDate;
        vo.etddate = firstNonEmpty(formatDate(request.getEtd()), rateDate);
        vo.etadate = formatDate(request.getEta());

        // Booking: vo.termservice[0] = TxtTransServiceCode (code), not transport name
        vo.service = EMPTY;
        if (vo.termservice != null && vo.termservice.length > 0) {
            vo.termservice[0] = resolveTransportServiceCode(request.getTransportService());
        }

        if (vo.cust == null || vo.cust.length == 0) {
            vo.cust = new String[Math.max(totcnt, 1)];
        }
        vo.cust[0] = text(request.getAgrPartyCode());

        vo.tos = EMPTY;
        vo.oog = anyOog(request) ? YES : NO;

        // Carriage haulage radios → flags (Booking OC/OM/PC/PM/N)
        vo.preflag = toOnCarriageFlag(request.getOnCarriage());
        vo.onflag = toPreCarriageFlag(request.getPreCarriage());
        // Booking: premode/onmode from getPolmodemodified (route inland modes), not C/M/N.
        // Default empty; applyRouteDetail fills from Routedetail orgMode/DelMode.
        // If route has no mode, fall back to request carriage value (Estimate-only safety).
        vo.premode = EMPTY;
        vo.onmode = EMPTY;

        // Defaults overwritten by applyRouteDetail when route row exists
        vo.loadterminal = EMPTY;
        vo.dischterminal = EMPTY;
        vo.HaultypeC = EMPTY;
        vo.HaultypeH = EMPTY;
        vo.traship1 = EMPTY;
        vo.traship2 = EMPTY;
        vo.traship3 = EMPTY;
        vo.traship4 = EMPTY;
        vo.traship5 = EMPTY;
        vo.trashipter1 = EMPTY;
        vo.trashipter2 = EMPTY;
        vo.trashipter3 = EMPTY;
        vo.trashipter4 = EMPTY;
        vo.trashipter5 = EMPTY;

        applyRouteDetail(vo, route);

        // If Routedetail had no inland mode, keep a non-blank value from request carriage
        if (text(vo.premode).isEmpty()) {
            vo.premode = carriageValue(request.getPreCarriage());
        }
        if (text(vo.onmode).isEmpty()) {
            vo.onmode = carriageValue(request.getOnCarriage());
        }

        // --- Per-container arrays ---
        for (int t = 0; t < lines.size(); t++) {
            ContainerLine line = lines.get(t);
            RateEstimateContainerRequest container = line.container;
            RateEstimateCommodityRequest commodity = line.commodity;

            vo.eqptype[t] = text(container.getCode());
            vo.eqpid[t] = line.eqpId;
            vo.commoditycode[t] = commodity != null ? text(commodity.getCommodityGroup()) : EMPTY;
            vo.weight[t] = resolveWeightTonnes(container, commodity);

            if (vo.exportfree != null && t < vo.exportfree.length) {
                vo.exportfree[t] = EMPTY;
            }
            if (vo.importfree != null && t < vo.importfree.length) {
                vo.importfree[t] = EMPTY;
            }
            if (vo.oog1 != null && t < vo.oog1.length) {
                vo.oog1[t] = boolToYn(container.getIsOOG(), NO);
            }
            if (vo.nor != null && t < vo.nor.length) {
                vo.nor[t] = boolToYn(container.getIsNOR(), NO);
            }
            if (vo.soc != null && t < vo.soc.length) {
                vo.soc[t] = boolToYn(container.getIsSOC(), NO);
            }
            if (vo.flexi != null && t < vo.flexi.length) {
                vo.flexi[t] = NO;
            }
            if (vo.refflag != null && t < vo.refflag.length) {
                vo.refflag[t] = resolveReeferFlag(container);
            }

            boolean isDg = Boolean.TRUE.equals(container.getIsDG())
                    || (commodity != null && commodity.getDangerousGoodsInfo() != null);
            if (vo.dgclass != null && t < vo.dgclass.length) {
                if (isDg) {
                    String dgClass = resolveDgClass(commodity);
                    // Booking ADG format: class~eqpId when IMO rows present
                    vo.dgclass[t] = dgClass.length() > 0
                            ? dgClass + "~" + line.eqpId
                            : EMPTY;
                } else {
                    vo.dgclass[t] = EMPTY;
                }
            }
            if (vo.dgstatus != null && t < vo.dgstatus.length) {
                vo.dgstatus[t] = isDg ? YES : NO;
            }
        }

        return vo;
    }

    /**
     * Applies ReportDB {@code Routedetail} fields the same way Booking fills
     * terminals / MainSvc / transshipments / haulage types from the routing grid.
     */
    private void applyRouteDetail(surchargeVO vo, RouteDetail route) {
        if (vo == null || route == null) {
            return;
        }

        vo.loadterminal = text(route.getPolTerminal());
        vo.dischterminal = text(route.getPodTerminal());
        vo.service = text(route.getMainSvc());

        // Booking: getPolmodemodified(Origin,POL,"P") / getPolmodemodified(POD,Delivery,"N")
        // Practical stand-in from Routedetail (same columns Booking route master uses)
        String preHaulMode = firstNonEmpty(text(route.getOrgMode()), text(route.getPolMode()));
        String onHaulMode = text(route.getDelMode());
        vo.premode = preHaulMode;
        vo.onmode = onHaulMode;
        vo.HaultypeC = preHaulMode;
        vo.HaultypeH = onHaulMode;

        setTraship(vo, 1, route.getOpsPol1(), route.getOpsPol1Ter());
        setTraship(vo, 2, route.getOpsPol2(), route.getOpsPol2Ter());
        setTraship(vo, 3, route.getOpsPol3(), route.getOpsPol3Ter());
        setTraship(vo, 4, route.getOpsPol4(), route.getOpsPol4Ter());
        setTraship(vo, 5, route.getOpsPol5(), route.getOpsPol5Ter());
    }

    private void setTraship(surchargeVO vo, int index, String port, String terminal) {
        String p = text(port);
        if (p.isEmpty()) {
            return;
        }
        String ter = text(terminal);
        switch (index) {
            case 1:
                vo.traship1 = p;
                vo.trashipter1 = ter;
                break;
            case 2:
                vo.traship2 = p;
                vo.trashipter2 = ter;
                break;
            case 3:
                vo.traship3 = p;
                vo.trashipter3 = ter;
                break;
            case 4:
                vo.traship4 = p;
                vo.trashipter4 = ter;
                break;
            case 5:
                vo.traship5 = p;
                vo.trashipter5 = ter;
                break;
            default:
                break;
        }
    }

    private String carriageValue(CarriageType carriage) {
        return carriage == null ? EMPTY : text(carriage.getValue());
    }

    private enum RouteField {
        ORIGIN, POL, POD, DELIVERY
    }

    private String routeValue(RouteDetail route, RouteField field) {
        if (route == null || field == null) {
            return EMPTY;
        }
        switch (field) {
            case ORIGIN:
                return text(route.getOrigin());
            case POL:
                return text(route.getPol());
            case POD:
                return text(route.getPod());
            case DELIVERY:
                return text(route.getDelivery());
            default:
                return EMPTY;
        }
    }

    /**
     * Booking: one cargo equipment ID per container unit ({@code quantity}).
     * Commodities hang off that equipment — they must not multiply {@code eqpid[]}
     * (that double-counted A-flag surcharges BAF/EIF/EPF in {@code toResponse}).
     */
    private List<ContainerLine> expandLines(RateEstimateRequest request) {
        List<ContainerLine> lines = new ArrayList<>();
        int eqpSeq = 1;
        for (RateEstimateContainerRequest container : request.getContainers()) {
            if (container == null) {
                continue;
            }
            int quantity = container.getQuantity() == null || container.getQuantity() < 1
                    ? 1
                    : container.getQuantity();
            RateEstimateCommodityRequest primary = primaryCommodity(container.getCommodities());
            for (int q = 0; q < quantity; q++) {
                lines.add(new ContainerLine(container, primary, String.valueOf(eqpSeq++)));
            }
        }
        return lines;
    }

    /** First DG commodity when present; otherwise first non-null commodity (empty placeholder if none). */
    private RateEstimateCommodityRequest primaryCommodity(List<RateEstimateCommodityRequest> commodities) {
        if (commodities == null || commodities.isEmpty()) {
            return new RateEstimateCommodityRequest();
        }
        for (RateEstimateCommodityRequest commodity : commodities) {
            if (commodity != null && commodity.getDangerousGoodsInfo() != null) {
                return commodity;
            }
        }
        for (RateEstimateCommodityRequest commodity : commodities) {
            if (commodity != null) {
                return commodity;
            }
        }
        return new RateEstimateCommodityRequest();
    }

    private String resolveRateDate(RateEstimateRequest request) {
        // Booking uses rate validity / ATD-at-POL; Estimate: cargoReadyDate → etd → eta
        return firstNonEmpty(
                formatDate(request.getCargoReadyDate()),
                formatDate(request.getEtd()),
                formatDate(request.getEta()));
    }

    private float resolveWeightTonnes(RateEstimateContainerRequest container,
                                      RateEstimateCommodityRequest commodity) {
        BigDecimal weight = null;
        String unit = "MT";

        // Prefer commodity weight + unit (Booking convertTonnes path).
        // Fall back to container grossWeight using the commodity unit when present
        // (Estimate clients often send grossWeight in the same unit as cargoWeightUnit).
        if (commodity != null && commodity.getCargoWeight() != null) {
            weight = commodity.getCargoWeight();
            unit = text(commodity.getCargoWeightUnit());
            if (unit.isEmpty()) {
                unit = "MT";
            }
        } else if (container != null && container.getGrossWeight() != null) {
            weight = container.getGrossWeight();
            if (commodity != null) {
                String commodityUnit = text(commodity.getCargoWeightUnit());
                if (!commodityUnit.isEmpty()) {
                    unit = commodityUnit;
                }
            }
        }

        if (weight == null) {
            return 0.0f;
        }
        return convertTonnes(weight.floatValue(), unit);
    }

    /**
     * Mirrors Booking {@code convertTonnes} for common units.
     */
    private float convertTonnes(float value, String unit) {
        if (unit == null) {
            return value;
        }
        String u = unit.trim().toUpperCase(Locale.ENGLISH);
        if ("KG".equals(u) || "KGS".equals(u)) {
            return value / 1000.0f;
        }
        if ("LB".equals(u) || "LBS".equals(u)) {
            return value * 0.000453592f;
        }
        // MT / TON / empty — already tonnes
        return value;
    }

    private String resolveDgClass(RateEstimateCommodityRequest commodity) {
        if (commodity == null || commodity.getDangerousGoodsInfo() == null) {
            return EMPTY;
        }
        RateEstimateDangerousGoodsRequest dg = commodity.getDangerousGoodsInfo();
        return text(dg.getClassCode());
    }

    private String resolveReeferFlag(RateEstimateContainerRequest container) {
        // Booking: reefer equipment type "R" → "y", else "n"
        if (container != null && EquipmentType.R.equals(container.getType())
                && !Boolean.TRUE.equals(container.getIsNOR())) {
            return "y";
        }
        return "n";
    }

    private boolean anyOog(RateEstimateRequest request) {
        for (RateEstimateContainerRequest container : request.getContainers()) {
            if (container != null && Boolean.TRUE.equals(container.getIsOOG())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Booking on-carriage haulage → preflag.
     */
    private String toOnCarriageFlag(CarriageType onCarriage) {
        if (onCarriage == null || CarriageType.N.equals(onCarriage)) {
            return "N";
        }
        if (CarriageType.C.equals(onCarriage)) {
            return "OC";
        }
        if (CarriageType.M.equals(onCarriage)) {
            return "OM";
        }
        return "N";
    }

    /**
     * Booking pre-carriage haulage → onflag.
     */
    private String toPreCarriageFlag(CarriageType preCarriage) {
        if (preCarriage == null || CarriageType.N.equals(preCarriage)) {
            return "N";
        }
        if (CarriageType.C.equals(preCarriage)) {
            return "PC";
        }
        if (CarriageType.M.equals(preCarriage)) {
            return "PM";
        }
        return "N";
    }

    private String formatDate(OffsetDateTime value) {
        if (value == null) {
            return EMPTY;
        }
        return SURCHARGE_DATE_FORMAT.format(value);
    }

    private String boolToYn(Boolean value, String defaultYn) {
        if (value == null) {
            return defaultYn;
        }
        return value.booleanValue() ? YES : NO;
    }

    private String firstNonEmpty(String... values) {
        if (values == null) {
            return EMPTY;
        }
        for (String value : values) {
            String t = text(value);
            if (t.length() > 0) {
                return t;
            }
        }
        return EMPTY;
    }

    /**
     * Booking {@code newAutoSurcharge}: {@code vo.termservice[0] = TxtTransServiceCode}.
     * Estimate request sends transport <em>name</em> (e.g. {@code CY/CY}); resolve to
     * {@code transportservice.trptsvccode}. Lookup miss → empty (do not pass the name).
     */
    private String resolveTransportServiceCode(String transportServiceName) {
        String input = text(transportServiceName);
        if (input.isEmpty()) {
            return EMPTY;
        }
        String code = lookupCache.getTransportServiceCode(input);
        return code == null || code.isEmpty() ? EMPTY : code;
    }

    private String text(String value) {
        if (value == null || "null".equalsIgnoreCase(value.trim())) {
            return EMPTY;
        }
        return value.trim();
    }

    private static final class ContainerLine {
        private final RateEstimateContainerRequest container;
        private final RateEstimateCommodityRequest commodity;
        private final String eqpId;

        private ContainerLine(RateEstimateContainerRequest container,
                              RateEstimateCommodityRequest commodity,
                              String eqpId) {
            this.container = container;
            this.commodity = commodity;
            this.eqpId = eqpId;
        }
    }
}
