package com.solverminds.ecom.register.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.constants.DateFormatConstants;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ecom_reg_details")
public class EcomRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "reg_no", length = 20)
    private String regNo; // generated

    @Column(name = "Cust_Code", nullable = false, length = 15)
    private String custCode;

    @Column(name = "Comp_Name", nullable = false, length = 150)
    private String compName;

    @Column(name = "Address", nullable = false, length = 500)
    private String address;

    @Column(name = "City", nullable = false, length = 150)
    private String city;

    @Column(name = "control_location", nullable = false, length = 10)
    private String controlLocation;

    @Column(name = "Country", nullable = false, length = 2)
    private String country;

    @Column(name = "Pin_Code", nullable = false, length = 20)
    private String pinCode;

    @Column(name = "Comp_Phone", length = 25)
    private String compPhone;

    @Column(name = "Comp_Domain", length = 50)
    private String compDomain;

    @Column(name = "loginname", nullable = false, length = 100)
    private String loginName;

    @Column(name = "password", nullable = false, length = 250)
    private String password; // store as-is per current requirement

    @Column(name = "First_Name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "Last_Name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "User_Phone", length = 25)
    private String userPhone;

    @Column(name = "User_Fax", length = 25)
    private String userFax;

    @Column(name = "User_Email", nullable = false, length = 100)
    private String userEmail;

    @Column(name = "prefLang", nullable = false, length = 2)
    private String prefLang;

    @Column(name = "User_Mobile", length = 25)
    private String userMobile;

    @Column(name = "Title", length = 15)
    private String title;

    @Column(name = "agencycode", nullable = false, length = 10)
    private String agencyCode;

    @Column(name = "random_number", length = 100)
    private String randomNumber;

    @Column(name = "registered_Date")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime registeredDate;

    @Column(name = "recentbl", length = 150)
    private String recentbl;

    @Column(name = "Address2", length = 500)
    private String address2;

    @Column(name = "preferredTimezone", length = 150)
    private String preferredTimezone;

    @Column(name = "State", length = 4)
    private String state;

    @Column(name = "ActionBy", length = 20)
    private String actionBy;

    @Column(name = "ActionDate")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime actionDate;

    @Column(name = "CodeAdvisedByAgency", length = 12)
    private String codeAdvisedByAgency;

    @Column(name = "StatusAdviseByAgency", length = 1)
    private String statusAdviseByAgency;

    @Column(name = "ActualLastLoginDate")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime actualLastLoginDate;

    @Column(name = "Remarks", length = 1008)
    private String remarks;

    @Column(name = "agencyReqType", length = 1)
    private String agencyReqType;

    @Column(name = "Expired_Date")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime expiredDate;

    @Column(name = "transferDoc", length = 1)
    private String transferDoc;

    // Defaulted business columns
    @Column(name = "type", length = 1, nullable = false)
    private String type; // default 'N'

    @Column(name = "isValid", length = 1, nullable = false)
    private String isValid; // default 'P'

    @Column(name = "status", length = 1, nullable = false)
    private String status; // default 'A'

    @Column(name = "city_other", length = 1, nullable = false)
    private String cityOther; // default 'Y'

    @Column(name = "RegType", length = 1, nullable = false)
    private String regType; // default 'S'

    @PrePersist
    public void prePersist() {
        if (this.type == null) this.type = "N";
        if (this.isValid == null) this.isValid = "P";  // "P" = New Registration (Pending Approval)
        if (this.status == null) this.status = "A";
        if (this.cityOther == null) this.cityOther = "Y";
        if (this.regType == null) this.regType = "S";
        this.registeredDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getControlLocation() {
        return controlLocation;
    }

    public void setControlLocation(String controlLocation) {
        this.controlLocation = controlLocation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCompPhone() {
        return compPhone;
    }

    public void setCompPhone(String compPhone) {
        this.compPhone = compPhone;
    }

    public String getCompDomain() {
        return compDomain;
    }

    public void setCompDomain(String compDomain) {
        this.compDomain = compDomain;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserFax() {
        return userFax;
    }

    public void setUserFax(String userFax) {
        this.userFax = userFax;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPrefLang() {
        return prefLang;
    }

    public void setPrefLang(String prefLang) {
        this.prefLang = prefLang;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
    }

    public LocalDateTime getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(LocalDateTime registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getRecentbl() {
        return recentbl;
    }

    public void setRecentbl(String recentbl) {
        this.recentbl = recentbl;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPreferredTimezone() {
        return preferredTimezone;
    }

    public void setPreferredTimezone(String preferredTimezone) {
        this.preferredTimezone = preferredTimezone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCityOther() {
        return cityOther;
    }

    public void setCityOther(String cityOther) {
        this.cityOther = cityOther;
    }

    public String getRegType() {
        return regType;
    }

    public void setRegType(String regType) {
        this.regType = regType;
    }

    public String getActionBy() {
        return actionBy;
    }

    public void setActionBy(String actionBy) {
        this.actionBy = actionBy;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public String getCodeAdvisedByAgency() {
        return codeAdvisedByAgency;
    }

    public void setCodeAdvisedByAgency(String codeAdvisedByAgency) {
        this.codeAdvisedByAgency = codeAdvisedByAgency;
    }

    public String getStatusAdviseByAgency() {
        return statusAdviseByAgency;
    }

    public void setStatusAdviseByAgency(String statusAdviseByAgency) {
        this.statusAdviseByAgency = statusAdviseByAgency;
    }

    public LocalDateTime getActualLastLoginDate() {
        return actualLastLoginDate;
    }

    public void setActualLastLoginDate(LocalDateTime actualLastLoginDate) {
        this.actualLastLoginDate = actualLastLoginDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getAgencyReqType() {
        return agencyReqType;
    }

    public void setAgencyReqType(String agencyReqType) {
        this.agencyReqType = agencyReqType;
    }

    public LocalDateTime getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getTransferDoc() {
        return transferDoc;
    }

    public void setTransferDoc(String transferDoc) {
        this.transferDoc = transferDoc;
    }
}
