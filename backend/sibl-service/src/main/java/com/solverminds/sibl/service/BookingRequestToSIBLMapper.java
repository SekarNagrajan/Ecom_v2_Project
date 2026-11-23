package com.solverminds.sibl.service;

import com.solverminds.sibl.dto.*;
import com.solverminds.sibl.util.VoyageCodeUtil;
import com.svm.ecom.common.enums.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps sibl's BookingRequestDTO to SIBLRequestDTO (same shape as SI create payload).
 * View-by-bookingNo uses booking-common entities via BookingCommonToSIBLMapper.
 */
public final class BookingRequestToSIBLMapper {

    private BookingRequestToSIBLMapper() {}

    public static SIBLRequestDTO toSIBLRequestDTO(BookingRequestDTO booking) {
        if (booking == null) {
            return null;
        }
        SIBLRequestDTO dto = new SIBLRequestDTO();

        // Primary identification: booking has no onlineSINo when from booking view
        dto.setOnlineSINo(booking.getOnlineSINo());
        dto.setBlNo(emptyToNull(booking.getBlNo()) != null ? booking.getBlNo() : booking.getBlNumber());
        dto.setBookingNumber(emptyToNull(booking.getBookingNo()) != null ? booking.getBookingNo() : booking.getBookNo());

        // Vessel & voyage
        dto.setVesselCode(emptyToNull(booking.getVessel()));
        dto.setVoyageCode(emptyToNull(VoyageCodeUtil.normalizeToNumeric(booking.getVoyage())));
        dto.setLoadBound(booking.getBound());

        // Ports
        dto.setLoadPortId(emptyToNull(booking.getLoadPortId()) != null ? booking.getLoadPortId() : booking.getPol());
        dto.setDischargePortId(emptyToNull(booking.getDischargePortId()) != null ? booking.getDischargePortId() : booking.getPod());
        dto.setOriginPortId(emptyToNull(booking.getPla()));
        dto.setFinalPortId(emptyToNull(booking.getPld()));

        // User & audit
        dto.setCustomerCodeFromJWT(emptyToNull(booking.getCustCode()));
        dto.setCreatedUser(emptyToNull(booking.getCrUser()));
        dto.setCreatedDate(parseLocalDateTime(booking.getCrDate()));
        dto.setChangeUser(emptyToNull(booking.getChgUser()));
        dto.setChangeDate(parseLocalDateTime(booking.getChgDate()));

        // BL version
        dto.setEditBl(parseInt(booking.getEditBL(), 0));
        dto.setBlVersion(parseInt(booking.getBlVersion(), 0));

        // Status & remarks
        dto.setStatus(booking.getStatus());
        dto.setAdditionalRemarks(emptyToNull(booking.getCustRemarks()));

        // Header names
        dto.setPlaHeaderName(emptyToNull(booking.getPlaHeaderName()));
        dto.setPolHeaderName(emptyToNull(booking.getPolHeaderName()));
        dto.setPodHeaderName(emptyToNull(booking.getPodHeaderName()));
        dto.setPldHeaderName(emptyToNull(booking.getPldHeaderName()));

        // Party blocks from Party or fallback
        if (booking.getShipper() != null) {
            dto.setShipperAddress(emptyToNull(booking.getShipper().getAddress()));
            dto.setShipperName(emptyToNull(booking.getShipper().getName()));
        }
        if (booking.getConsignee() != null) {
            dto.setConsigeeAddress(emptyToNull(booking.getConsignee().getAddress()));
            dto.setConsigeeName(emptyToNull(booking.getConsignee().getName()));
        }
        if (booking.getNotifyParty() != null) {
            dto.setNotifyAddress(emptyToNull(booking.getNotifyParty().getAddress()));
            dto.setNotifyName(emptyToNull(booking.getNotifyParty().getName()));
        }

        // Collections
        dto.setCustomers(mapCustomers(booking.getCustomers()));
        dto.setContainers(mapContainers(booking.getContainers()));
        dto.setCommodities(mapCommodities(booking.getCommodities()));
        // When booking has container DG, set hazardous=Y and dangerousGoods on first commodity of that container (DG at commodity level)
        applyContainerDgToFirstCommodityFromRequest(dto.getCommodities(), booking.getContainers());

        return dto;
    }

    /** For each booking container with dangerousGoodsflag=Y or dangerousGoods set, set hazardous=Y and dangerousGoods on first matching commodity. */
    private static void applyContainerDgToFirstCommodityFromRequest(List<SIBLCommodityDTO> commodities, List<BookingRequestDTO.ContainerSummary> containers) {
        if (commodities == null || containers == null) return;
        for (BookingRequestDTO.ContainerSummary c : containers) {
            boolean hasDg = c.getDangerousGoodsflag() == YesNoFlag.Y || c.getDangerousGoods() != null;
            if (!hasDg) continue;
            Integer cargoNo = c.getCargoNo() != null ? c.getCargoNo() : 1;
            String containerNo = emptyToNull(c.getContainerNo());
            for (SIBLCommodityDTO com : commodities) {
                if (cargoNo.equals(com.getCargoNo()) && (containerNo == null || containerNo.equals(com.getContainerNo()))) {
                    if (com.getHazardous() != YesNoFlag.Y) {
                        com.setHazardous(YesNoFlag.Y);
                        if (com.getDangerousGoods() == null && c.getDangerousGoods() != null) {
                            SIBLDangerousGoodsDTO dg = new SIBLDangerousGoodsDTO();
                            dg.setMarinePollutant(c.getDangerousGoods().getMarinePollutant());
                            dg.setUnNo(c.getDangerousGoods().getUnNo());
                            dg.setImdgClass(c.getDangerousGoods().getClassCode());
                            dg.setFlashPt(c.getDangerousGoods().getFlashPt());
                            dg.setFlashPtUnit(c.getDangerousGoods().getFlashPtUnit());
                            com.setDangerousGoods(dg);
                        } else if (com.getDangerousGoods() == null) {
                            com.setDangerousGoods(new SIBLDangerousGoodsDTO());
                        }
                    }
                    break;
                }
            }
        }
    }

    private static String emptyToNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return s.trim();
    }

    private static java.time.LocalDateTime parseLocalDateTime(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return java.time.LocalDateTime.parse(s.replace(" ", "T").substring(0, Math.min(19, s.length())));
        } catch (Exception e) {
            return null;
        }
    }

    private static List<SIBLCustomerDTO> mapCustomers(List<BookingRequestDTO.CustomerSummary> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<SIBLCustomerDTO> out = new ArrayList<>(list.size());
        for (BookingRequestDTO.CustomerSummary c : list) {
            SIBLCustomerDTO dto = new SIBLCustomerDTO();
            dto.setCustomerType(c.getCustomerType());
            dto.setCustomerCode(emptyToNull(c.getCustomerCode()));
            dto.setCountryId(emptyToNull(c.getCountryId()));
            dto.setCustomerName(emptyToNull(c.getCustomerName()));
            dto.setAddress(emptyToNull(c.getAddress()));
            dto.setAddress2(emptyToNull(c.getAddress1()));
            dto.setCity(emptyToNull(c.getCity()));
            dto.setTelephone(emptyToNull(c.getTelephone()));
            dto.setEmail(emptyToNull(c.getEmail()));
            out.add(dto);
        }
        return out;
    }

    private static List<SIBLContainerDTO> mapContainers(List<BookingRequestDTO.ContainerSummary> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<SIBLContainerDTO> out = new ArrayList<>(list.size());
        for (BookingRequestDTO.ContainerSummary c : list) {
            SIBLContainerDTO dto = new SIBLContainerDTO();
            dto.setCargoNo(c.getCargoNo() != null ? c.getCargoNo() : 1);
            dto.setEquipmentSize(emptyToNull(c.getEqpSize()));
            dto.setContainerNo(emptyToNull(c.getContainerNo()));
            dto.setSocFlag(c.getSocFlag());
            dto.setCarrierSeal(emptyToNull(c.getCarrierSeal()));
            dto.setShipperSeal(emptyToNull(c.getShipperSeal()));
            dto.setTareWeight(parseIntNullable(c.getTareWeight()));
            dto.setCargoWeight(parseInt(c.getCargoWeight(), 0));
            dto.setGrossweight(parseIntNullable(c.getGrossweight()));
            dto.setWeightUnit(c.getWeightUnit());
            YesNoFlag nor = c.getNor() != null ? c.getNor() : c.getRef_nor();
            dto.setNor(nor);
            EquipmentStatus eqpStatus = c.getEquipmentTypestatus();
            dto.setEquipmentStatus(eqpStatus);
            dto.setOogFlag(c.getOogFlag());
            if (c.getReefer() != null) {
                dto.setReefer(YesNoFlag.Y);
                SIBLReeferDTO reefer = new SIBLReeferDTO();
                reefer.setTempSet(c.getReefer().getSetTemp());
                reefer.setTempMin(c.getReefer().getMinTemp());
                reefer.setTempMax(c.getReefer().getMaxTemp());
                reefer.setTempSetUnit(c.getReefer().getTempUnit());
                dto.setReeferSettings(reefer);
            }
            // DG at commodity level only; container DG from booking is applied to first commodity in applyContainerDgToFirstCommodityFromRequest
            out.add(dto);
        }
        return out;
    }

    private static Integer parseIntNullable(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseInt(String s, int defaultVal) {
        Integer v = parseIntNullable(s);
        return v != null ? v : defaultVal;
    }

    private static List<SIBLCommodityDTO> mapCommodities(List<BookingRequestDTO.CommoditySummary> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<SIBLCommodityDTO> out = new ArrayList<>(list.size());
        for (BookingRequestDTO.CommoditySummary c : list) {
            SIBLCommodityDTO dto = new SIBLCommodityDTO();
            dto.setCargoNo(c.getCargoNo() != null ? c.getCargoNo() : 1);
            dto.setContainerNo(emptyToNull(c.getContainerNo()));
            dto.setItemNo(c.getItemNo() != null ? c.getItemNo() : 1);
            dto.setCommodityGroup(emptyToNull(c.getCommodityGroup()));
            dto.setPackageType(emptyToNull(c.getPackageType()));
            dto.setPackageQty(parseInt(c.getPackageQty(), 0));
            dto.setItemWeight(parseIntNullable(c.getItemWeight()));
            dto.setItemWeightUnit(c.getItemWeightUnit());
            dto.setNettwt(parseIntNullable(c.getNettwt()));
            dto.setItemVolume(parseDouble(c.getItemVolume(), null));
            dto.setItemVolumeUnit(c.getVolumnUnit());
            dto.setHsCode(emptyToNull(c.getHsCode()));
            dto.setMarksDesc(emptyToNull(c.getDescription()));
            dto.setCommodityHBLNo(emptyToNull(c.getCommodityHBLNo()));
            dto.setBuyerCode(emptyToNull(c.getBuyerCode()));
            dto.setSellerCode(emptyToNull(c.getSellerCode()));
            dto.setBuyerName(emptyToNull(c.getBuyerName()));
            dto.setSellerName(emptyToNull(c.getSellerName()));
            if (c.getOog() != null) {
                SIBLOogDTO oog = new SIBLOogDTO();
                oog.setLeftFt(c.getOog().getItemLeft());
                oog.setRightFt(c.getOog().getItemRight());
                oog.setForward(c.getOog().getItemForward());
                oog.setAft(c.getOog().getItemAft());
                oog.setTopFt(c.getOog().getItemTop());
                oog.setUnit(c.getOog().getItemLengthUnit());
                dto.setOog(oog);
            }
            if (c.getDangerousGoods() != null && !c.getDangerousGoods().isEmpty()) {
                BookingRequestDTO.DangerousGoodsSummary first = c.getDangerousGoods().get(0);
                SIBLDangerousGoodsDTO dg = new SIBLDangerousGoodsDTO();
                dg.setMarinePollutant(first.getMarinePollutant());
                dg.setUnNo(first.getUnNo());
                dg.setImdgClass(first.getClassCode());
                dg.setFlashPt(first.getFlashPt());
                dg.setFlashPtUnit(first.getFlashPtUnit());
                dto.setDangerousGoods(dg);
            }
            out.add(dto);
        }
        return out;
    }

    private static Double parseDouble(String s, Double defaultVal) {
        if (s == null || s.trim().isEmpty()) return defaultVal;
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static SiStatus safeSiStatus(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return SiStatus.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static Bound safeBound(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Bound.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static YesNoFlag safeYesNoFlag(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return YesNoFlag.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static EquipmentStatus safeEquipmentStatus(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return EquipmentStatus.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static WeightUnit safeWeightUnit(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return WeightUnit.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static VolumeUnit safeVolumeUnit(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return VolumeUnit.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static TemperatureUnit safeFlashPtUnit(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return TemperatureUnit.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static CustomerType safeCustomerType(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return CustomerType.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static TemperatureUnit safeTemperatureUnit(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return TemperatureUnit.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static LengthUnit safeLengthUnit(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return LengthUnit.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
}
