package com.solverminds.booking.validation;

import com.solverminds.booking.dto.BookingRequestDTO;
import com.solverminds.booking.dto.CommodityDTO;
import com.solverminds.booking.dto.ContainerDTO;

import javax.validation.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates Bean Validation paths on the V1 persistence model {@link BookingRequestDTO} into
 * V2 JSON pointer-style locations (same convention as {@code BookingV2ExceptionHandler}: {@code #/field[0].nested}).
 * Used when validating the mapped DTO with the same rules as {@code BookingController} (V1 API).
 */
public final class V1BookingViolationToV2PointerMapper {

    private static final Pattern INDEXED = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)\\[(\\d+)]$");

    private V1BookingViolationToV2PointerMapper() {
    }

    /**
     * Converts a Bean Validation path on {@link BookingRequestDTO} to a V2-relative pointer (without leading {@code #/}).
     */
    public static String toV2RelativePointer(Path propertyPath, BookingRequestDTO dto) {
        String dotPath = BeanValidationPathFormatter.toDotPath(propertyPath);
        if (dotPath == null || dotPath.isEmpty()) {
            return "";
        }
        return translateDotPath(dotPath, dto);
    }

    static String translateDotPath(String dotPath, BookingRequestDTO dto) {
        List<Segment> segments = parseSegments(dotPath);
        if (segments.isEmpty()) {
            return "";
        }
        return translateFrom(0, segments, dto);
    }

    private static List<Segment> parseSegments(String dotPath) {
        List<Segment> out = new ArrayList<>();
        for (String part : dotPath.split("\\.", -1)) {
            if (part.isEmpty()) {
                continue;
            }
            Matcher m = INDEXED.matcher(part);
            if (m.matches()) {
                out.add(new Segment(m.group(1), Integer.parseInt(m.group(2))));
            } else {
                out.add(new Segment(part, null));
            }
        }
        return out;
    }

    private static String translateFrom(int start, List<Segment> segments, BookingRequestDTO dto) {
        if (start >= segments.size()) {
            return "";
        }
        Segment head = segments.get(start);

        switch (head.name) {
            case "originPortId":
                return join("originId", tail(start, segments));
            case "finalPortId":
                return join("deliveryId", tail(start, segments));
            case "loadPortId":
                return join("polId", tail(start, segments));
            case "dischargePortId":
                return join("podId", tail(start, segments));
            case "routeId":
                return join("route.id", tail(start, segments));
            case "rateRefNo":
                return join("contractReference", tail(start, segments));
            case "customerPO":
                return join("customerReference", tail(start, segments));
            case "ensFilling":
                return join("ensFilingType", tail(start, segments));
            case "t2lFilling":
                return join("t2lFiling", tail(start, segments));
            case "routes":
                return translateRoutes(head, start, segments, dto);
            case "commodities":
                return translateCommodities(head, start, segments, dto);
            case "customers":
                return translateCustomers(head, start, segments);
            case "containers":
                return translateContainers(head, start, segments);
            case "files":
                return rebuildFrom(start, segments);
            default:
                return rebuildFrom(start, segments);
        }
    }

    private static String translateRoutes(Segment head, int start, List<Segment> segments, BookingRequestDTO dto) {
        if (head.index == null) {
            return rebuildFrom(start, segments);
        }
        String legSuffix = "";
        if (start + 1 < segments.size()) {
            legSuffix = translateRouteLegFieldPath(segments.subList(start + 1, segments.size()));
            if (!legSuffix.isEmpty()) {
                legSuffix = "." + legSuffix;
            }
        }
        return "route.legs[" + head.index + "]" + legSuffix;
    }

    private static String translateRouteLegFieldPath(List<Segment> rest) {
        if (rest.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rest.size(); i++) {
            Segment s = rest.get(i);
            if (i > 0) {
                sb.append('.');
            }
            if (s.index != null) {
                sb.append(mapRouteLegField(s.name)).append('[').append(s.index).append(']');
            } else {
                sb.append(mapRouteLegField(s.name));
            }
        }
        return sb.toString();
    }

    private static String mapRouteLegField(String v1) {
        switch (v1) {
            case "loadTerminal":
                return "loadTerminalId";
            case "dischargeTerminal":
                return "dischargeTerminalId";
            case "dischType":
                return "dischargeType";
            case "etdDate":
                return "etd";
            case "etaDate":
                return "eta";
            default:
                return v1;
        }
    }

    private static String translateCommodities(Segment head, int start, List<Segment> segments, BookingRequestDTO dto) {
        if (head.index == null) {
            return rebuildFrom(start, segments);
        }
        int[] nest = resolveFlatCommodityToNested(dto, head.index);
        if (nest == null) {
            return rebuildFrom(start, segments);
        }
        String prefix = "containers[" + nest[0] + "].commodities[" + nest[1] + "]";
        if (start + 1 >= segments.size()) {
            return prefix;
        }
        String sub = translateCommodityTail(segments.subList(start + 1, segments.size()));
        return sub.isEmpty() ? prefix : prefix + "." + sub;
    }

    private static String translateCommodityTail(List<Segment> rest) {
        if (rest.isEmpty()) {
            return "";
        }
        if ("dangerousGoods".equals(rest.get(0).name) || "dangerousGoodsInfo".equals(rest.get(0).name)) {
            StringBuilder dg = new StringBuilder("dangerousGoodsInfo");
            for (int i = 1; i < rest.size(); i++) {
                dg.append('.').append(mapDgField(rest.get(i).name));
            }
            return dg.toString();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rest.size(); i++) {
            Segment s = rest.get(i);
            if (i > 0) {
                sb.append('.');
            }
            if (s.index != null) {
                sb.append(mapCommodityField(s.name)).append('[').append(s.index).append(']');
            } else {
                sb.append(mapCommodityField(s.name));
            }
        }
        return sb.toString();
    }

    private static String mapCommodityField(String v1) {
        switch (v1) {
            case "packageQty":
                return "packageQuantity";
            case "volumnUnit":
                return "cargoVolumeUnit";
            case "nettwt":
                return "netWeight";
            case "itemWeight":
                return "cargoWeight";
            case "itemWeightUnit":
                return "cargoWeightUnit";
            default:
                return v1;
        }
    }

    private static String mapDgField(String v1) {
        switch (v1) {
            case "flashPt":
                return "flashPoint";
            case "flashPtUnit":
                return "flashPointUnit";
            case "marinePollutant":
            case "isMarinePollutant":
                return "isMarinePollutant";
            case "classCode":
                return "imoClass";
            case "outerPackCode":
                return "outerPacking.code";
            case "innerPackCode":
                return "innerPacking.code";
            case "outerPackQty":
                return "outerPackingQuantity";
            case "innerPackQty":
                return "innerPackingQuantity";
            case "grossWeight":
                return "grossWeight";
            case "netWeight":
                return "netWeight";
            case "netExplosiveWeight":
                return "netExplosiveWeight";
            case "contactName":
                return "emergencyContact.contactName";
            case "emgCountryCode":
                return "emergencyContact.countryCode";
            case "emgAreaCode":
                return "emergencyContact.areaCode";
            case "emgLocalNumber":
                return "emergencyContact.localNumber";
            case "limitedQuantity":
                return "isLimitedQuantity";
            case "exceptedQuantity":
                return "isExceptedQuantity";
            case "reportableQuantity":
                return "isReportableQuantity";
            case "waste":
                return "isWaste";
            default:
                return v1;
        }
    }

    private static String translateCustomers(Segment head, int start, List<Segment> segments) {
        if (head.index == null) {
            return rebuildFrom(start, segments);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("customers[").append(head.index).append("]");
        for (int i = start + 1; i < segments.size(); i++) {
            Segment s = segments.get(i);
            sb.append('.');
            if ("countryId".equals(s.name)) {
                sb.append("address.countryCode");
            } else if ("city".equals(s.name)) {
                sb.append("address.locality");
            } else if ("zipCode".equals(s.name)) {
                sb.append("address.postalCode");
            } else if ("address".equals(s.name)) {
                sb.append("address.lines[0]");
            } else if ("address1".equals(s.name)) {
                sb.append("address.lines[1]");
            } else {
                sb.append(mapCustomerField(s.name));
            }
        }
        return sb.toString();
    }

    private static String mapCustomerField(String v1) {
        switch (v1) {
            case "customerType":
                return "type";
            case "customerCode":
                return "code";
            case "customerName":
                return "name";
            default:
                return v1;
        }
    }

    private static String translateContainers(Segment head, int start, List<Segment> segments) {
        if (head.index == null) {
            return rebuildFrom(start, segments);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("containers[").append(head.index).append("]");
        for (int i = start + 1; i < segments.size(); i++) {
            Segment s = segments.get(i);
            sb.append('.');
            if ("reefer".equals(s.name)) {
                sb.append("reeferSettings");
                for (int j = i + 1; j < segments.size(); j++) {
                    sb.append('.').append(mapReeferField(segments.get(j).name));
                }
                break;
            }
            if (s.index != null) {
                sb.append(mapContainerField(s.name)).append('[').append(s.index).append(']');
            } else {
                sb.append(mapContainerField(s.name));
            }
        }
        return sb.toString();
    }

    private static String mapContainerField(String v1) {
        switch (v1) {
            case "equipmentQty":
                return "quantity";
            case "equipmentType":
                return "type";
            case "equipmentSize":
                return "code";
            case "equipmentStatus":
                return "status";
            case "oogFlag":
                return "isOOG";
            case "socFlag":
                return "isSOC";
            case "dgFlag":
                return "isDG";
            case "nor":
                return "isNOR";
            case "containerNo":
                return "id";
            default:
                return v1;
        }
    }

    private static String mapReeferField(String v1) {
        switch (v1) {
            case "setTemp":
                return "temperature";
            case "tempUnit":
                return "temperatureUnit";
            case "minTemp":
            case "maxTemp":
            case "ventilation":
            case "ventSet":
            case "humidity":
            case "humidityFlag":
            case "drains":
            case "o2Value":
            case "co2Value":
                return v1;
            default:
                return v1;
        }
    }

    private static String join(String mappedHead, List<Segment> tail) {
        if (tail.isEmpty()) {
            return mappedHead;
        }
        return mappedHead + "." + rebuildSegments(tail);
    }

    private static List<Segment> tail(int start, List<Segment> segments) {
        if (start + 1 >= segments.size()) {
            return Collections.emptyList();
        }
        return segments.subList(start + 1, segments.size());
    }

    private static String rebuildFrom(int start, List<Segment> segments) {
        return rebuildSegments(segments.subList(start, segments.size()));
    }

    private static String rebuildSegments(List<Segment> segments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            Segment s = segments.get(i);
            if (i > 0) {
                sb.append('.');
            }
            if (s.index != null) {
                sb.append(s.name).append('[').append(s.index).append(']');
            } else {
                sb.append(s.name);
            }
        }
        return sb.toString();
    }

    /**
     * Maps flat {@code commodities} list index to nested {@code containers[ci].commodities[mi]} using {@code cargoNo}.
     */
    static int[] resolveFlatCommodityToNested(BookingRequestDTO dto, int flatIndex) {
        List<CommodityDTO> commodities = dto.getCommodities();
        List<ContainerDTO> containers = dto.getContainers();
        if (commodities == null || flatIndex < 0 || flatIndex >= commodities.size() || containers == null) {
            return null;
        }
        CommodityDTO co = commodities.get(flatIndex);
        if (co == null) {
            return null;
        }
        Integer cargoNo = co.getCargoNo();
        if (cargoNo == null) {
            return null;
        }
        int ci = -1;
        for (int i = 0; i < containers.size(); i++) {
            ContainerDTO ct = containers.get(i);
            if (ct != null && cargoNo.equals(ct.getCargoNo())) {
                ci = i;
                break;
            }
        }
        if (ci < 0) {
            return null;
        }
        int mi = 0;
        for (int j = 0; j < flatIndex; j++) {
            CommodityDTO prev = commodities.get(j);
            if (prev != null && cargoNo.equals(prev.getCargoNo())) {
                mi++;
            }
        }
        return new int[]{ci, mi};
    }

    static final class Segment {
        final String name;
        final Integer index;

        Segment(String name, Integer index) {
            this.name = name;
            this.index = index;
        }
    }
}
