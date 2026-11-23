package com.solverminds.booking.mapper;

import com.solverminds.booking.dto.DangerousGoodsDTO;
import com.solverminds.booking.dto.v2.DangerousGoodsInfoV2;
import com.solverminds.booking.dto.v2.DgEmergencyContactV2;
import com.solverminds.booking.dto.v2.DgPackagingReferenceV2;
import com.solverminds.booking.entity.EcomBookingDg;
import com.svm.ecom.common.enums.TemperatureUnit;
import com.svm.ecom.common.enums.YesNoFlag;
import org.springframework.stereotype.Component;

/**
 * Maps V2 DangerousGoodsInfo ↔ internal DTO ↔ ecombooking_dg entity.
 */
@Component
public class DangerousGoodsMapper {

    public DangerousGoodsDTO toInternal(DangerousGoodsInfoV2 v2) {
        if (v2 == null) {
            return null;
        }
        DangerousGoodsDTO d = new DangerousGoodsDTO();
        d.setUnNo(v2.getUnNo());
        d.setClassCode(v2.getImoClass());
        if (v2.getFlashPoint() != null) {
            d.setFlashPt(v2.getFlashPoint().intValue());
        }
        d.setFlashPtUnit(v2.getFlashPointUnit());
        d.setMarinePollutant(boolToYesNo(v2.getIsMarinePollutant()));
        d.setVariant(v2.getVariant());
        d.setImdgAmendment(v2.getImdgAmendment());
        d.setTechnicalName(v2.getTechnicalName());
        d.setSubRisk1(v2.getSubRisk1());
        d.setSubRisk2(v2.getSubRisk2());
        d.setPackagingGroup(v2.getPackagingGroup());
        d.setEms(v2.getEms());
        d.setState(v2.getState());
        d.setProperShippingName(v2.getProperShippingName());
        d.setStowageInstruction(v2.getStowageInstruction());
        d.setImoStow(v2.getImoStow());
        d.setSegregation(v2.getSegregation());
        if (v2.getEmergencyContact() != null) {
            d.setContactName(v2.getEmergencyContact().getContactName());
            d.setEmgCountryCode(v2.getEmergencyContact().getCountryCode());
            d.setEmgAreaCode(v2.getEmergencyContact().getAreaCode());
            d.setEmgLocalNumber(v2.getEmergencyContact().getLocalNumber());
        }
        if (v2.getOuterPacking() != null) {
            d.setOuterPackCode(v2.getOuterPacking().getCode());
            d.setOuterPackDesc(v2.getOuterPacking().getName());
        }
        d.setOuterPackQty(v2.getOuterPackingQuantity());
        if (v2.getInnerPacking() != null) {
            d.setInnerPackCode(v2.getInnerPacking().getCode());
            d.setInnerPackDesc(v2.getInnerPacking().getName());
        }
        d.setInnerPackQty(v2.getInnerPackingQuantity());
        d.setGrossWeight(v2.getGrossWeight());
        d.setNetWeight(v2.getNetWeight());
        d.setNetExplosiveWeight(v2.getNetExplosiveWeight());
        d.setFullPackagingCode(v2.getFullPackagingCode());
        d.setSadt(v2.getSadt());
        d.setControlTemperature(v2.getControlTemperature());
        d.setEmergencyTemperature(v2.getEmergencyTemperature());
        d.setTemperatureUnit(v2.getTemperatureUnit());
        d.setLimitedQuantity(boolToYesNo(v2.getIsLimitedQuantity()));
        d.setExceptedQuantity(boolToYesNo(v2.getIsExceptedQuantity()));
        d.setReportableQuantity(boolToYesNo(v2.getIsReportableQuantity()));
        d.setWaste(boolToYesNo(v2.getIsWaste()));
        return d;
    }

    public DangerousGoodsInfoV2 toV2(DangerousGoodsDTO d) {
        if (d == null) {
            return null;
        }
        DangerousGoodsInfoV2 v2 = new DangerousGoodsInfoV2();
        v2.setUnNo(d.getUnNo());
        v2.setImoClass(d.getClassCode());
        if (d.getFlashPt() != null) {
            v2.setFlashPoint(d.getFlashPt().doubleValue());
        }
        v2.setFlashPointUnit(d.getFlashPtUnit());
        v2.setIsMarinePollutant(yesNoToBool(d.getMarinePollutant()));
        v2.setVariant(d.getVariant());
        v2.setImdgAmendment(d.getImdgAmendment());
        v2.setTechnicalName(d.getTechnicalName());
        v2.setSubRisk1(d.getSubRisk1());
        v2.setSubRisk2(d.getSubRisk2());
        v2.setPackagingGroup(d.getPackagingGroup());
        v2.setEms(d.getEms());
        v2.setState(d.getState());
        v2.setProperShippingName(d.getProperShippingName());
        v2.setStowageInstruction(d.getStowageInstruction());
        v2.setImoStow(d.getImoStow());
        v2.setSegregation(d.getSegregation());
        if (hasAny(d.getContactName(), d.getEmgCountryCode(), d.getEmgAreaCode(), d.getEmgLocalNumber())) {
            DgEmergencyContactV2 ec = new DgEmergencyContactV2();
            ec.setContactName(d.getContactName());
            ec.setCountryCode(d.getEmgCountryCode());
            ec.setAreaCode(d.getEmgAreaCode());
            ec.setLocalNumber(d.getEmgLocalNumber());
            v2.setEmergencyContact(ec);
        }
        if (d.getOuterPackCode() != null || d.getOuterPackDesc() != null) {
            DgPackagingReferenceV2 op = new DgPackagingReferenceV2();
            op.setCode(d.getOuterPackCode());
            op.setName(d.getOuterPackDesc());
            v2.setOuterPacking(op);
        }
        v2.setOuterPackingQuantity(d.getOuterPackQty());
        if (d.getInnerPackCode() != null || d.getInnerPackDesc() != null) {
            DgPackagingReferenceV2 ip = new DgPackagingReferenceV2();
            ip.setCode(d.getInnerPackCode());
            ip.setName(d.getInnerPackDesc());
            v2.setInnerPacking(ip);
        }
        v2.setInnerPackingQuantity(d.getInnerPackQty());
        v2.setGrossWeight(d.getGrossWeight());
        v2.setNetWeight(d.getNetWeight());
        v2.setNetExplosiveWeight(d.getNetExplosiveWeight());
        v2.setFullPackagingCode(d.getFullPackagingCode());
        v2.setSadt(d.getSadt());
        v2.setControlTemperature(d.getControlTemperature());
        v2.setEmergencyTemperature(d.getEmergencyTemperature());
        v2.setTemperatureUnit(d.getTemperatureUnit());
        v2.setIsLimitedQuantity(yesNoToBool(d.getLimitedQuantity()));
        v2.setIsExceptedQuantity(yesNoToBool(d.getExceptedQuantity()));
        v2.setIsReportableQuantity(yesNoToBool(d.getReportableQuantity()));
        v2.setIsWaste(yesNoToBool(d.getWaste()));
        return v2;
    }

    public EcomBookingDg toEntity(DangerousGoodsDTO d, String ebookno, Integer conid, String containerNo) {
        EcomBookingDg e = new EcomBookingDg();
        e.setEbookno(ebookno);
        e.setConid(conid);
        e.setItemno(d.getItemNo());
        e.setContainerno(containerNo);
        applyDtoToEntity(d, e);
        return e;
    }

    public void applyDtoToEntity(DangerousGoodsDTO d, EcomBookingDg e) {
        e.setMarinePollutant(flagValue(d.getMarinePollutant()));
        e.setUnNo(trim(d.getUnNo()));
        e.setClassCode(trim(d.getClassCode()));
        e.setFlashPt(d.getFlashPt() != null ? d.getFlashPt().floatValue() : null);
        e.setFlashPtUnit(d.getFlashPtUnit() != null ? d.getFlashPtUnit().getValue() : null);
        e.setVariant(trim(d.getVariant()));
        e.setImdgAmendment(trim(d.getImdgAmendment()));
        e.setTechnicalName(trim(d.getTechnicalName()));
        e.setSubRisk1(trim(d.getSubRisk1()));
        e.setSubRisk2(trim(d.getSubRisk2()));
        e.setPackagingGroup(trim(d.getPackagingGroup()));
        e.setEms(trim(d.getEms()));
        e.setState(trim(d.getState()));
        e.setProperShippingName(trim(d.getProperShippingName()));
        e.setStowageInstruction(trim(d.getStowageInstruction()));
        e.setImoStow(trim(d.getImoStow()));
        e.setSegregation(trim(d.getSegregation()));
        e.setContactName(trim(d.getContactName()));
        e.setEmgCountryCode(trim(d.getEmgCountryCode()));
        e.setEmgAreaCode(trim(d.getEmgAreaCode()));
        e.setEmgLocalNumber(trim(d.getEmgLocalNumber()));
        e.setOuterPackCode(trim(d.getOuterPackCode()));
        e.setOuterPackDesc(trim(d.getOuterPackDesc()));
        e.setOuterPackQty(d.getOuterPackQty());
        e.setInnerPackCode(trim(d.getInnerPackCode()));
        e.setInnerPackDesc(trim(d.getInnerPackDesc()));
        e.setInnerPackQty(d.getInnerPackQty());
        e.setGrossWt(toFloat(d.getGrossWeight()));
        e.setNetWt(toFloat(d.getNetWeight()));
        e.setNetExplWt(toFloat(d.getNetExplosiveWeight()));
        e.setFullPackagingCode(trim(d.getFullPackagingCode()));
        e.setSadtTemp(doubleToString(d.getSadt()));
        e.setControlTemp(doubleToString(d.getControlTemperature()));
        e.setEmergencyTemp(doubleToString(d.getEmergencyTemperature()));
        e.setTempUnit(d.getTemperatureUnit() != null ? d.getTemperatureUnit().getValue() : null);
        e.setLimitedQty(flagValue(d.getLimitedQuantity()));
        e.setExceptedQty(flagValue(d.getExceptedQuantity()));
        e.setReportableQty(flagValue(d.getReportableQuantity()));
        e.setWaste(flagValue(d.getWaste()));
    }

    public DangerousGoodsDTO fromEntity(EcomBookingDg e) {
        DangerousGoodsDTO d = new DangerousGoodsDTO();
        d.setCargoNo(e.getConid());
        d.setContainerNo(e.getContainerno());
        d.setItemNo(e.getItemno());
        d.setMarinePollutant(safeYesNo(e.getMarinePollutant()));
        d.setUnNo(e.getUnNo());
        d.setFlashPt(e.getFlashPt() != null ? e.getFlashPt().intValue() : null);
        d.setFlashPtUnit(TemperatureUnit.fromValue(e.getFlashPtUnit()));
        d.setClassCode(e.getClassCode());
        d.setVariant(e.getVariant());
        d.setImdgAmendment(e.getImdgAmendment());
        d.setTechnicalName(e.getTechnicalName());
        d.setSubRisk1(e.getSubRisk1());
        d.setSubRisk2(e.getSubRisk2());
        d.setPackagingGroup(e.getPackagingGroup());
        d.setEms(e.getEms());
        d.setState(e.getState());
        d.setProperShippingName(e.getProperShippingName());
        d.setStowageInstruction(e.getStowageInstruction());
        d.setImoStow(e.getImoStow());
        d.setSegregation(e.getSegregation());
        d.setContactName(e.getContactName());
        d.setEmgCountryCode(e.getEmgCountryCode());
        d.setEmgAreaCode(e.getEmgAreaCode());
        d.setEmgLocalNumber(e.getEmgLocalNumber());
        d.setOuterPackCode(e.getOuterPackCode());
        d.setOuterPackDesc(e.getOuterPackDesc());
        d.setOuterPackQty(e.getOuterPackQty());
        d.setInnerPackCode(e.getInnerPackCode());
        d.setInnerPackDesc(e.getInnerPackDesc());
        d.setInnerPackQty(e.getInnerPackQty());
        d.setGrossWeight(toDouble(e.getGrossWt()));
        d.setNetWeight(toDouble(e.getNetWt()));
        d.setNetExplosiveWeight(toDouble(e.getNetExplWt()));
        d.setFullPackagingCode(e.getFullPackagingCode());
        d.setSadt(parseDouble(e.getSadtTemp()));
        d.setControlTemperature(parseDouble(e.getControlTemp()));
        d.setEmergencyTemperature(parseDouble(e.getEmergencyTemp()));
        d.setTemperatureUnit(TemperatureUnit.fromValue(e.getTempUnit()));
        d.setLimitedQuantity(safeYesNo(e.getLimitedQty()));
        d.setExceptedQuantity(safeYesNo(e.getExceptedQty()));
        d.setReportableQuantity(safeYesNo(e.getReportableQty()));
        d.setWaste(safeYesNo(e.getWaste()));
        return d;
    }

    public DangerousGoodsInfoV2 copy(DangerousGoodsInfoV2 src) {
        if (src == null) {
            return null;
        }
        return toV2(toInternal(src));
    }

    private static YesNoFlag boolToYesNo(Boolean b) {
        if (b == null) {
            return null;
        }
        return b ? YesNoFlag.Y : YesNoFlag.N;
    }

    private static Boolean yesNoToBool(YesNoFlag f) {
        if (f == null) {
            return null;
        }
        return f == YesNoFlag.Y;
    }

    private static String flagValue(YesNoFlag f) {
        return f != null ? f.getValue() : null;
    }

    private static YesNoFlag safeYesNo(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return YesNoFlag.fromValue(s.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private static String trim(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }

    private static Float toFloat(Double d) {
        return d == null ? null : d.floatValue();
    }

    private static Double toDouble(Float f) {
        return f == null ? null : f.doubleValue();
    }

    private static String doubleToString(Double d) {
        return d == null ? null : String.valueOf(d);
    }

    private static Double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static boolean hasAny(String... values) {
        if (values == null) {
            return false;
        }
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
