package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.svm.ecom.common.enums.CustomerType;
import com.solverminds.sibl.util.DtoUtil;
import com.solverminds.sibl.dto.validation.ValidSIBLCustomerMasterData;
import com.solverminds.sibl.dto.validation.ValidSIBLCustomerType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ValidSIBLCustomerType
@ValidSIBLCustomerMasterData
@Schema(name = "SIBLCustomerDTO", description = "Customer/party details associated with the SI")
public class SIBLCustomerDTO {

    // ===== Hidden / server-managed =====
    @Schema(hidden = true) @JsonIgnore
    private String onlineSINo;    // eSINo, server link

    @Schema(hidden = true) @JsonIgnore
    private String zipCode;       // zipCode (hidden in JSON)

    @Schema(hidden = true) @JsonIgnore
    private String fax;           // fax (hidden in JSON)

    @Schema(hidden = true) @JsonIgnore
    private String flag;          // flag (hidden in JSON)

    @Schema(hidden = true) @JsonIgnore
    private String toorder;       // toorder (hidden in JSON)

    @Schema(hidden = true) @JsonIgnore
    private String eori;          // eori (hidden in JSON)

    @Schema(hidden = true) @JsonIgnore
    private String persontype;    // persontype (hidden in JSON)

    // ===== Visible fields =====
    @NotNull(message = "customerType is required")
    @Schema(description = "Customer/party type. Allowed for SI: SH, CN, NT, N1, N2, FW, SL, BY (Shipper, Consignee, Notify, Notify Party 1/2, Freight Forwarder, Seller, Buyer).",
            example = "SH", required = true)
    private CustomerType customerType;   // custTypeId (ecomSI_cust.custTypeId)

    @NotBlank(message = "Customer code must be provided (non-empty).")
    @Pattern(regexp = "^[0-9A-Za-z_-]*$", message = "customerCode must be alphanumeric with optional _ or -")
    @Schema(description = "Customer code (from customermaster).", example = "20001374", required = true)
    private String customerCode;   // custId

    @NotBlank(message = "Country ID must be provided (non-empty).")
    @Pattern(regexp = "^[A-Z]{2}$", message = "countryId must be ISO 3166-1 alpha-2 (e.g., IN, CN)")
    @Schema(description = "Country ID (ISO 3166-1 alpha-2).", example = "CN", required = true)
    private String countryId;      // countryId

    @NotBlank(message = "Customer name must be provided (non-empty).")
    @Size(max = 200)
    @Schema(description = "Customer name / legal entity.", example = "ECOM TESTING", required = true)
    private String customerName;   // name

    @NotBlank(message = "Address must be provided (non-empty).")
    @Size(max = 255)
    @Schema(description = "Primary address line.", example = "513B, Building A, Free Trade Financial Center", required = true)
    private String address;        // address1

    @Schema(description = "Secondary address / additional line.", example = "603103")
    private String address2;       // address2

    @NotBlank(message = "City must be provided (non-empty).")
    @Size(max = 120)
    @Schema(description = "City of the customer/party.", example = "Beijiao, China", required = true)
    private String city;           // city

    @Schema(description = "Telephone number with country code.", 
            example = "86-38-00138000")
    private String telephone;      // telephone

    @Email
    @Schema(description = "Email address.", 
            example = "dhiman.p@solverminds.com")
    private String email;          // email

    @Schema(description = "State/Province identifier if applicable.", 
            example = "")
    private String stateId;        // stateId

    public SIBLCustomerDTO() {}

    // ===== Getters & Setters =====
    public String getOnlineSINo() { return DtoUtil.emptyIfNull(onlineSINo); }
    public void setOnlineSINo(String onlineSINo) { this.onlineSINo = onlineSINo; }

    public String getZipCode() { return DtoUtil.emptyIfNull(zipCode); }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getFax() { return DtoUtil.emptyIfNull(fax); }
    public void setFax(String fax) { this.fax = fax; }

    public String getFlag() { return DtoUtil.emptyIfNull(flag); }
    public void setFlag(String flag) { this.flag = flag; }

    public String getToorder() { return DtoUtil.emptyIfNull(toorder); }
    public void setToorder(String toorder) { this.toorder = toorder; }

    public String getEori() { return DtoUtil.emptyIfNull(eori); }
    public void setEori(String eori) { this.eori = eori; }

    public String getPersontype() { return DtoUtil.emptyIfNull(persontype); }
    public void setPersontype(String persontype) { this.persontype = persontype; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public String getCustomerCode() { return DtoUtil.emptyIfNull(customerCode); }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getCountryId() { return DtoUtil.emptyIfNull(countryId); }
    public void setCountryId(String countryId) { this.countryId = countryId; }

    public String getCustomerName() { return DtoUtil.emptyIfNull(customerName); }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getAddress() { return DtoUtil.emptyIfNull(address); }
    public void setAddress(String address) { this.address = address; }

    public String getAddress2() { return DtoUtil.emptyIfNull(address2); }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getCity() { return DtoUtil.emptyIfNull(city); }
    public void setCity(String city) { this.city = city; }

    public String getTelephone() { return DtoUtil.emptyIfNull(telephone); }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return DtoUtil.emptyIfNull(email); }
    public void setEmail(String email) { this.email = email; }

    public String getStateId() { return DtoUtil.emptyIfNull(stateId); }
    public void setStateId(String stateId) { this.stateId = stateId; }
}