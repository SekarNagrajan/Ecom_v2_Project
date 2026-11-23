package com.solverminds.sibl.service;

import com.solverminds.booking.common.entity.*;
import com.solverminds.sibl.dto.*;
import com.solverminds.sibl.util.VoyageCodeUtil;
import com.svm.ecom.common.enums.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps booking-common entities (booking, booking_leg, booking_cust, booking_cargo, booking_commodity, booking_referen)
 * to SIBLRequestDTO for view-by-bookingNo (same data as booking view).
 */
public final class BookingCommonToSIBLMapper {

    private BookingCommonToSIBLMapper() {}

    /**
     * Build SIBLRequestDTO from booking-common entities (Documentation DB).
     */
    public static SIBLRequestDTO fromBookingCommon(
            Booking booking,
            List<BookingLeg> legs,
            List<BookingCust> custList,
            List<BookingCargo> cargoList,
            List<BookingCommodity> commodityList,
            List<BookingReference> refList) {
        if (booking == null) return null;
        SIBLRequestDTO dto = new SIBLRequestDTO();
        String bookNo = booking.getBookNo();

        // booking table -> SIBLResponseDTO (Direct/Audit)
        dto.setOnlineSINo(null);
        dto.setBlNo(null);
        dto.setBookingNumber(bookNo);
        dto.setCustomerCodeFromJWT(custCodeFromFirstCust(custList));
        dto.setOriginPortId(emptyToNull(booking.getOriginPortId()));
        dto.setLoadPortId(emptyToNull(booking.getLoadPortId()));
        dto.setDischargePortId(emptyToNull(booking.getDischargePortId()));
        dto.setFinalPortId(emptyToNull(booking.getFinalPortId()));
        dto.setStatus(safeSiStatus(emptyToNull(booking.getStatus())));
        dto.setAdditionalRemarks(emptyToNull(booking.getRemarks()));
        dto.setDeclinedRemarks(emptyToNull(booking.getCustRemarks()));
        dto.setCreatedDate(booking.getDatecr());
        dto.setChangeDate(booking.getDateup());
        dto.setCreatedUser(emptyToNull(booking.getUsercr()));
        dto.setChangeUser(emptyToNull(booking.getUserup()));
        dto.setAgencyRefNo(emptyToNull(booking.getAgencyRefNo()));
        dto.setCargoProtectId(booking.getCargoProtectId());
        dto.setT2lFilling(safeYesNoFlag(booking.getT2lFiling()));
        dto.setEnsType(safeEnsType(emptyToNull(booking.getEnsType())));
        dto.setEnsFilling(safeEnsFillingType(emptyToNull(booking.getEnsFiling())));

        // booking_referen -> acid, packingList, invoiceUpload (refId ACI, PAC, INU)
        mapReferences(refList, dto);

        // booking table -> vesselCode, voyageCode, loadBound, serviceCode, groupCallId, polTerminal, podTerminal (per DTO mapping: booking table columns)
        dto.setVesselCode(emptyToNull(booking.getVesselCode()));
        dto.setVoyageCode(emptyToNull(VoyageCodeUtil.normalizeToNumeric(booking.getVoyageCode())));
        dto.setLoadBound(safeBound(emptyToNull(booking.getBound())));
        dto.setServiceCode(emptyToNull(booking.getServiceCode()));
        dto.setGroupCallId(emptyToNull(booking.getCallId()));
        dto.setPolTerminal(emptyToNull(booking.getPolTerminal()));
        dto.setPodTerminal(emptyToNull(booking.getPodTerminal()));

        dto.setCustomers(mapCustomers(custList));
        dto.setContainers(mapContainers(cargoList));
        dto.setCommodities(mapCommodities(commodityList));
        // When booking has container dgFlag=Y, set hazardous=Y and dangerousGoods on first commodity of that container (DG at commodity level)
        applyContainerDgToFirstCommodity(dto.getCommodities(), cargoList);
        return dto;
    }

    /** For each booking cargo with dgFlag=Y, set hazardous=Y and dangerousGoods (empty DTO) on first matching commodity. */
    private static void applyContainerDgToFirstCommodity(List<SIBLCommodityDTO> commodities, List<BookingCargo> cargoList) {
        if (commodities == null || cargoList == null) return;
        for (BookingCargo c : cargoList) {
            if (!"Y".equalsIgnoreCase(emptyToNull(c.getDgFlag()))) continue;
            Integer cargoNo = c.getCargoNo() != null ? c.getCargoNo() : 1;
            String eqpId = emptyToNull(c.getEqpId());
            for (SIBLCommodityDTO com : commodities) {
                if (cargoNo.equals(com.getCargoNo()) && (eqpId == null || eqpId.equals(com.getContainerNo()))) {
                    if (com.getHazardous() != YesNoFlag.Y) {
                        com.setHazardous(YesNoFlag.Y);
                        if (com.getDangerousGoods() == null) com.setDangerousGoods(new SIBLDangerousGoodsDTO());
                    }
                    break;
                }
            }
        }
    }

    private static String custCodeFromFirstCust(List<BookingCust> list) {
        if (list == null || list.isEmpty()) return null;
        return emptyToNull(list.get(0).getCustId());
    }

    private static void mapReferences(List<BookingReference> refList, SIBLRequestDTO dto) {
        if (refList == null) return;
        for (BookingReference ref : refList) {
            if (ref.getRefId() == null) continue;
            String refId = ref.getRefId().trim().toUpperCase();
            String refVal = ref.getRefVal();
            if ("ACI".equals(refId)) dto.setAcid(refVal);
            else if ("PAC".equals(refId)) dto.setPackingList(refVal);
            else if ("INU".equals(refId)) dto.setInvoiceUpload(refVal);
        }
    }

    private static List<SIBLCustomerDTO> mapCustomers(List<BookingCust> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        List<SIBLCustomerDTO> out = new ArrayList<>(list.size());
        for (BookingCust c : list) {
            SIBLCustomerDTO dto = new SIBLCustomerDTO();
            dto.setCustomerType(safeCustomerType(c.getCustTypeId()));
            dto.setCustomerCode(emptyToNull(c.getCustId()));
            dto.setCountryId(emptyToNull(c.getCountryId()));
            dto.setCustomerName(emptyToNull(c.getName()));
            dto.setAddress(emptyToNull(c.getAddress1()));
            dto.setAddress2(emptyToNull(c.getAddress2()));
            dto.setCity(emptyToNull(c.getCity()));
            dto.setZipCode(emptyToNull(c.getZipCode()));
            dto.setTelephone(emptyToNull(c.getTelephone()));
            dto.setEmail(emptyToNull(c.getEmail()));
            out.add(dto);
        }
        return out;
    }

    private static List<SIBLContainerDTO> mapContainers(List<BookingCargo> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        List<SIBLContainerDTO> out = new ArrayList<>(list.size());
        for (BookingCargo c : list) {
            SIBLContainerDTO dto = new SIBLContainerDTO();
            dto.setCargoNo(c.getCargoNo() != null ? c.getCargoNo() : 1);
            dto.setEquipmentSize(emptyToNull(c.getEqpTypeId()));
            dto.setContainerNo(emptyToNull(c.getEqpId()));
            String type = emptyToNull(c.getType());
            dto.setEquipmentType(type != null ? EquipmentType.fromValue(type) : null);
            dto.setSocFlag(safeYesNoFlag(emptyToNull(c.getShipperOwn())));
            dto.setNor(safeYesNoFlag(emptyToNull(c.getNor())));
            dto.setOogFlag(safeYesNoFlag(emptyToNull(c.getOog())));
            dto.setEquipmentStatus(safeEquipmentStatus(emptyToNull(c.getEqpStatus())));
            dto.setTareWeight(c.getTareWeight() != null ? c.getTareWeight().intValue() : null);
            dto.setCargoWeight(c.getCargoWeight() != null ? c.getCargoWeight().intValue() : 0);
            dto.setGrossweight(c.getGrossWeight() != null ? c.getGrossWeight().intValue() : null);
            dto.setWeightUnit(safeWeightUnit(emptyToNull(c.getWeightUnit())));
            dto.setShipperSeal(emptyToNull(c.getSeal1No()));
            dto.setCarrierSeal(emptyToNull(c.getSeal2No()));
            dto.setRateEquipmentTypeId(emptyToNull(c.getRateEqpTypeId()));
            dto.setReefer("R".equalsIgnoreCase(c.getType()) ? YesNoFlag.Y : YesNoFlag.N);
            // Populate reefer details when reefer (type=R) so SI view shows reefer object per rule
            if ("R".equalsIgnoreCase(c.getType())) {
                dto.setReeferSettings(mapCargoToReefer(c));
            }
            // DG is at commodity level only; container-level DG from booking is applied to first commodity below
            out.add(dto);
        }
        return out;
    }

    /**
     * Map booking_cargo reefer fields to SIBLReeferDTO for SI view by booking number.
     * Mapping: tempmin?tempMin, tempmax?tempMax, tempunit?tempSetUnit, humidity?humidity,
     * humidity_flag?humidityFlag, drains?drains, co2value?co2Level, o2value?o2Level, ventpercentage?ventilation.
     */
    private static SIBLReeferDTO mapCargoToReefer(BookingCargo c) {
        SIBLReeferDTO r = new SIBLReeferDTO();
        if (c.getSetTemp() != null) r.setTempSet(c.getSetTemp());
        if (c.getTempMin() != null) r.setTempMin(c.getTempMin());
        if (c.getTempMax() != null) r.setTempMax(c.getTempMax());
        TemperatureUnit tempUnit = safeTemperatureUnit(c.getTempUnit());
        if (tempUnit != null) {
            r.setTempSetUnit(tempUnit);
        }
        if (c.getVentSetting() != null) r.setVentSet(c.getVentSetting());
        String ventPct = emptyToNull(c.getVentPercentage());
        if (ventPct != null) r.setVentilation(ventPct);
        String humidity = emptyToNull(c.getHumidity());
        if (humidity != null) {
            try { r.setHumidity(Double.parseDouble(humidity)); } catch (NumberFormatException ignored) {}
        }
        if (c.getHumidityFlag() != null) r.setHumidityFlag(c.getHumidityFlag() ? YesNoFlag.Y : YesNoFlag.N);
        if (emptyToNull(c.getDrains()) != null) r.setDrains(c.getDrains());
        String o2 = emptyToNull(c.getO2Value());
        if (o2 != null) { try { r.setO2Level(Double.parseDouble(o2)); } catch (NumberFormatException ignored) {} }
        String co2 = emptyToNull(c.getCo2Value());
        if (co2 != null) { try { r.setCo2Level(Double.parseDouble(co2)); } catch (NumberFormatException ignored) {} }
        return r;
    }

    /** Map booking_commodity OOG fields to SIBLOogDTO; returns null if no dimension present. */
    private static SIBLOogDTO mapCommodityToOog(BookingCommodity c) {
        if (c == null) return null;
        boolean hasAny = (c.getItemLeft() != null) || (c.getItemRight() != null) || (c.getItemForward() != null)
                || (c.getItemAft() != null) || (c.getItemTop() != null);
        if (!hasAny) return null;
        SIBLOogDTO oog = new SIBLOogDTO();
        if (c.getItemLeft() != null) oog.setLeftFt(c.getItemLeft());
        if (c.getItemRight() != null) oog.setRightFt(c.getItemRight());
        if (c.getItemForward() != null) oog.setForward(c.getItemForward());
        if (c.getItemAft() != null) oog.setAft(c.getItemAft());
        if (c.getItemTop() != null) oog.setTopFt(c.getItemTop());
        LengthUnit lengthUnit = safeLengthUnit(c.getItemLengthUnit());
        oog.setUnit(lengthUnit != null ? lengthUnit : LengthUnit.M);
        return oog;
    }

    private static List<SIBLCommodityDTO> mapCommodities(List<BookingCommodity> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();
        List<SIBLCommodityDTO> out = new ArrayList<>(list.size());
        for (BookingCommodity c : list) {
            SIBLCommodityDTO dto = new SIBLCommodityDTO();
            dto.setCargoNo(c.getCargoNo() != null ? c.getCargoNo() : 1);
            dto.setContainerNo(emptyToNull(c.getEqpId()));
            dto.setItemNo(c.getItemNo() != null ? c.getItemNo() : 1);
            dto.setCommodityGroup(emptyToNull(c.getCommodityGroup()));
            dto.setPackageType(emptyToNull(c.getPackageId()));
            dto.setPackageQty(c.getItemQty() != null ? c.getItemQty() : 0);
            dto.setItemWeight(c.getItemWeight() != null ? c.getItemWeight().intValue() : null);
            dto.setItemWeightUnit(safeWeightUnit(c.getItemWeightUnit()));
            dto.setItemVolume(c.getItemVolume() != null ? c.getItemVolume().doubleValue() : null);
            dto.setItemVolumeUnit(safeVolumeUnit(emptyToNull(c.getItemVolumeUnit())));
            dto.setNettwt(c.getNetWeight() != null ? c.getNetWeight().intValue() : null);
            dto.setHsCode(emptyToNull(c.getCommodityId()));
            dto.setMarksDesc(emptyToNull(c.getMarksDesc()));
            dto.setHazardous(safeYesNoFlag(emptyToNull(c.getHazardous())));
            dto.setCommodityHBLNo(emptyToNull(c.getCommodityHblNo()));
            dto.setBuyerCode(emptyToNull(c.getByCode()));
            dto.setSellerCode(emptyToNull(c.getSlCode()));
            dto.setBuyerName(emptyToNull(c.getByName()));
            dto.setSellerName(emptyToNull(c.getSlName()));
            // Populate existing oog (SIBLOogDTO) from booking_commodity: itemLeft?leftFt, itemRight?rightFt, itemForward?forward, itemAft?aft, itemTop?topFt, itemLengthUnit?unit (default M)
            SIBLOogDTO oog = mapCommodityToOog(c);
            if (oog != null) dto.setOog(oog);
            // Populate dangerousGoods when hazardous=Y so SI view shows dangerousGoods per rule (empty if booking has no DG fields)
            if ("Y".equalsIgnoreCase(emptyToNull(c.getHazardous()))) {
                dto.setDangerousGoods(new SIBLDangerousGoodsDTO());
            }
            out.add(dto);
        }
        return out;
    }

    private static String emptyToNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        return s.trim();
    }

    private static int parseInt(String s, int defaultVal) {
        if (s == null || s.trim().isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(s.trim());
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
    private static EnsFillingType safeEnsFillingType(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return EnsFillingType.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static EnsType safeEnsType(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return EnsType.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static FreightPaymentType safeFreightPaymentType(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return FreightPaymentType.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static TemperatureUnit safeFlashPtUnit(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return TemperatureUnit.fromValue(s.trim()); } catch (Exception e) { return null; }
    }
    private static VolumeUnit safeVolumeUnit(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return VolumeUnit.fromValue(s.trim()); } catch (Exception e) { return null; }
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
