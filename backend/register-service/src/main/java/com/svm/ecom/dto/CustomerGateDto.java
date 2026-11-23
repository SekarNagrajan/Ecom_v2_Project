package com.svm.ecom.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.constants.DateFormatConstants;
import com.svm.ecom.enums.GateStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;


@Schema(name = "CustomerGate",
        description = "ECOM Customer Gate record: registration/gate status for an e-commerce user. "
            + "LINE-level only \u2014 no agency concept, no agency-based row filtering.")
public class CustomerGateDto {

    @JsonProperty("custCode")
    @Schema(description = "Customer code ", example = "20001374", nullable = true)
    private String custCode;

    @JsonProperty("companyName")
    @Schema(description = "Company name ", example = "Acme Corporation", nullable = true)
    private String companyName;

    @JsonProperty("address")
    @Schema(description = "Address ", nullable = true)
    private String address;

    @JsonProperty("address1")
    @Schema(description = "Address2", nullable = true)
    private String address1;

    @JsonProperty("city")
    @Schema(description = "City", nullable = true)
    private String city;

    @JsonProperty("postalCode")
    @Schema(description = "Postal/pin code", nullable = true)
    private String postalCode;

    @JsonProperty("countryCode")
    @Schema(description = "Country code", nullable = true)
    private String countryCode;

    @JsonProperty("companyDomain")
    @Schema(description = "Company domain ", nullable = true)
    private String companyDomain;

    @JsonProperty("companyPhone")
    @Schema(description = "Company phone", nullable = true)
    private String companyPhone;

    @JsonProperty("firstName")
    @Schema(description = "First name",
        example = "Mr.Daniel", nullable = true)
    private String firstName;

    @JsonProperty("lastName")
    @Schema(description = "Last name ", nullable = true)
    private String lastName;

    @JsonProperty("userPhone")
    @Schema(description = "User phone ", nullable = true)
    private String userPhone;

    @JsonProperty("userMobile")
    @Schema(description = "User mobile ", nullable = true)
    private String userMobile;

    @JsonProperty("userFax")
    @Schema(description = "User fax", nullable = true)
    private String userFax;

    @JsonProperty("userEmail")
    @Schema(description = "User email", nullable = true)
    private String userEmail;

    @JsonProperty("gateStatus")
    @Schema(description = "Customer Gate status. One of N, E, I, P, M, R, Y.",
        implementation = GateStatus.class, nullable = true)
    private GateStatus gateStatus;

    @JsonProperty("recordStatus")
    @Schema(description = "Secondary status code ",
        nullable = true)
    private String recordStatus;

    @JsonProperty("regNo")
    @Schema(description = "Registration ID .", nullable = true)
    private String regNo;

    @JsonProperty("controlLocation")
    @Schema(description = "Control location ", nullable = true)
    private String controlLocation;

    @JsonProperty("registeredDate")
    @Schema(type = "string", format = "date-time",
        description = "Registration date/time",
        example = "2026-01-01T00:00:00Z", nullable = true)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime registeredDate;

    @JsonProperty("actionBy")
    @Schema(description = "User that last actioned the registration ", nullable = true)
    private String actionBy;

    @JsonProperty("actionDate")
    @Schema(type = "string", format = "date-time",
        description = "Date/time of last action",
        example = "2026-01-02T00:00:00Z", nullable = true)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime actionDate;

    @JsonProperty("remarks")
    @Schema(description = "Remarks / reason.", nullable = true)
    private String remarks;

    @JsonProperty("regType")
    @Schema(type = "string",
        description = "Registration type: M - Manual, S - System, A - Auto.",
        allowableValues = {"M", "S", "A"},
        example = "M", nullable = true)
    private String regType;

    @JsonProperty("recentBL")
    @Schema(description = "BL / booking number .", nullable = true)
    private String recentBL;

    @JsonProperty("lastLoginDate")
    @Schema(type = "string", format = "date-time",
        description = "Last login date/time",
        example = "2026-01-03T08:15:00Z", nullable = true)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime lastLoginDate;

    public CustomerGateDto() {
    }

    // ---- Getters / Setters (plain style, matches sibling DTOs e.g. BookingSummaryResponseDto) ----

    public String getCustCode() { return custCode; }
    public void setCustCode(String custCode) { this.custCode = custCode; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCompanyDomain() { return companyDomain; }
    public void setCompanyDomain(String companyDomain) { this.companyDomain = companyDomain; }

    public String getCompanyPhone() { return companyPhone; }
    public void setCompanyPhone(String companyPhone) { this.companyPhone = companyPhone; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserMobile() { return userMobile; }
    public void setUserMobile(String userMobile) { this.userMobile = userMobile; }

    public String getUserFax() { return userFax; }
    public void setUserFax(String userFax) { this.userFax = userFax; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public GateStatus getGateStatus() { return gateStatus; }
    public void setGateStatus(GateStatus gateStatus) { this.gateStatus = gateStatus; }

    public String getRecordStatus() { return recordStatus; }
    public void setRecordStatus(String recordStatus) { this.recordStatus = recordStatus; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getControlLocation() { return controlLocation; }
    public void setControlLocation(String controlLocation) { this.controlLocation = controlLocation; }

    public LocalDateTime getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDateTime registeredDate) { this.registeredDate = registeredDate; }

    public String getActionBy() { return actionBy; }
    public void setActionBy(String actionBy) { this.actionBy = actionBy; }

    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getRegType() { return regType; }
    public void setRegType(String regType) { this.regType = regType; }

    public String getRecentBL() { return recentBL; }
    public void setRecentBL(String recentBL) { this.recentBL = recentBL; }

    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    @Override
    public String toString() {
        return "CustomerGateDto{" +
                "custCode='" + custCode + '\'' +
                ", companyName='" + companyName + '\'' +
                ", gateStatus=" + gateStatus +
                ", recordStatus='" + recordStatus + '\'' +
                ", regNo='" + regNo + '\'' +
                ", regType=" + regType +
                '}';
    }
}
