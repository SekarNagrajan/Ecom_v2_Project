package com.solverminds.sibl.dto;

import com.solverminds.sibl.util.DtoUtil;
import com.svm.ecom.common.enums.EnsFillingType;
import com.svm.ecom.common.enums.EnsType;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * ENS Declarant details for ecomsi_ics_new table
 * Insert only if ENS filing type is provided
 */
@Schema(name = "SIBLEnsDeclarantDTO", description = "ENS Declarant details (required when ENS filing type is provided)")
public class SIBLEnsDeclarantDTO {

    @Schema(description = "EU Customs zone", example = "EU")
    private String euCustom;

    @Schema(description = "BL type for ENS", example = "HOUSE")
    private String billType;

    @Schema(description = "ENS type - S (Straight B/L) or C (Consolidated)", example = "S")
    private EnsType ensType;

    @Schema(description = "Payment method", example = "CREDIT")
    private String paymentMode;

    @Schema(description = "Declarant name", example = "ABC Logistics")
    private String custName;

    @Schema(description = "Address line 1", example = "123 Main Street")
    private String address;

    @Schema(description = "Address line 2", example = "Suite 100")
    private String address1;

    @Schema(description = "City", example = "Rotterdam")
    private String city;

    @Schema(description = "Country code", example = "NL")
    private String country;

    @Schema(description = "State", example = "South Holland")
    private String state;

    @Schema(description = "Phone number", example = "+31-10-1234567")
    private String telephone;

    @Schema(description = "Email address", example = "ens@abclogistics.com")
    private String email;

    @Schema(description = "EORI number", example = "NL123456789")
    private String ensEori;

    @Schema(description = "Filing type - S (Single Filing), P (Partial Filing), N (NA)", example = "S")
    private EnsFillingType filingType;

    @Schema(description = "ZIP/Postal code", example = "3011 AA")
    private String zipCode;

    // ===== Constructors =====
    public SIBLEnsDeclarantDTO() {}

    // ===== Getters & Setters =====
    public String getEuCustom() { return DtoUtil.emptyIfNull(euCustom); }
    public void setEuCustom(String euCustom) { this.euCustom = euCustom; }

    public String getBillType() { return DtoUtil.emptyIfNull(billType); }
    public void setBillType(String billType) { this.billType = billType; }

    public EnsType getEnsType() { return ensType; }
    public void setEnsType(EnsType ensType) { this.ensType = ensType; }

    public String getPaymentMode() { return DtoUtil.emptyIfNull(paymentMode); }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getCustName() { return DtoUtil.emptyIfNull(custName); }
    public void setCustName(String custName) { this.custName = custName; }

    public String getAddress() { return DtoUtil.emptyIfNull(address); }
    public void setAddress(String address) { this.address = address; }

    public String getAddress1() { return DtoUtil.emptyIfNull(address1); }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getCity() { return DtoUtil.emptyIfNull(city); }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return DtoUtil.emptyIfNull(country); }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return DtoUtil.emptyIfNull(state); }
    public void setState(String state) { this.state = state; }

    public String getTelephone() { return DtoUtil.emptyIfNull(telephone); }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return DtoUtil.emptyIfNull(email); }
    public void setEmail(String email) { this.email = email; }

    public String getEnsEori() { return DtoUtil.emptyIfNull(ensEori); }
    public void setEnsEori(String ensEori) { this.ensEori = ensEori; }

    public EnsFillingType getFilingType() { return filingType; }
    public void setFilingType(EnsFillingType filingType) { this.filingType = filingType; }

    public String getZipCode() { return DtoUtil.emptyIfNull(zipCode); }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}
