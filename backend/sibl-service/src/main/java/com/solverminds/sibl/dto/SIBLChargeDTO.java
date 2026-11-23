package com.solverminds.sibl.dto;

import com.solverminds.sibl.util.DtoUtil;
import com.svm.ecom.common.enums.CustomerType;
import com.svm.ecom.common.enums.FreightPaymentType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Charge details for SI charges table
 */
@Schema(name = "SIBLChargeDTO", description = "Charge details for SI")
public class SIBLChargeDTO {

    @Schema(description = "Charge code from master", example = "OFR", required = true)
    private String chargeId;

    @Schema(description = "Payment agency ID", example = "INMAA")
    private String payAgencyId;

    @Schema(description = "Customer type - AP (Agreement Party) or BP (Booking Party)", example = "AP")
    private CustomerType custTypeId;

    @Schema(description = "Prepaid/Collect flag - P (Prepaid) or C (Collect)", example = "P")
    private FreightPaymentType prepaidCollect;

    @Schema(description = "Equipment type ID", example = "20DC")
    private String eqpTypeId;

    // ===== Constructors =====
    public SIBLChargeDTO() {}

    // ===== Getters & Setters =====
    public String getChargeId() { return DtoUtil.emptyIfNull(chargeId); }
    public void setChargeId(String chargeId) { this.chargeId = chargeId; }

    public String getPayAgencyId() { return DtoUtil.emptyIfNull(payAgencyId); }
    public void setPayAgencyId(String payAgencyId) { this.payAgencyId = payAgencyId; }

    public CustomerType getCustTypeId() { return custTypeId; }
    public void setCustTypeId(CustomerType custTypeId) { this.custTypeId = custTypeId; }

    public FreightPaymentType getPrepaidCollect() { return prepaidCollect; }
    public void setPrepaidCollect(FreightPaymentType prepaidCollect) { this.prepaidCollect = prepaidCollect; }

    public String getEqpTypeId() { return DtoUtil.emptyIfNull(eqpTypeId); }
    public void setEqpTypeId(String eqpTypeId) { this.eqpTypeId = eqpTypeId; }
}
