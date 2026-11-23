package com.svm.ecom.master.repository.masters;

import java.util.Date;

/**
 * Row projection from customermaster for v2 customer list.
 */
public class CustomerMasterRow {

    private String custCode;
    private String custName;
    private String custposition;
    private String phone;
    private String email;
    private String custtype;
    private Date dtofCreation;
    private String persontype;
    private String mobileno;
    private String eori;
    private String fillingtype;
    private String crUsr;
    private Date crDate;
    private String chgUsr;
    private Date chgDate;
    private String address1;
    private String address2;
    private String city;
    private String zipCode;
    private String countryId;
    // Billing (customermaster)
    private String billAddressee;
    private String billPhone;
    private String billEmail;
    private String billmobileNo;
    private String billAddress1;
    private String billAddress2;
    private String billCity;
    private String billZipCode;
    private String billCountry;

    public String getCustCode() { return custCode; }
    public void setCustCode(String custCode) { this.custCode = custCode; }
    public String getCustName() { return custName; }
    public void setCustName(String custName) { this.custName = custName; }
    public String getCustposition() { return custposition; }
    public void setCustposition(String custposition) { this.custposition = custposition; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCusttype() { return custtype; }
    public void setCusttype(String custtype) { this.custtype = custtype; }
    public Date getDtofCreation() { return dtofCreation; }
    public void setDtofCreation(Date dtofCreation) { this.dtofCreation = dtofCreation; }
    public String getPersontype() { return persontype; }
    public void setPersontype(String persontype) { this.persontype = persontype; }
    public String getMobileno() { return mobileno; }
    public void setMobileno(String mobileno) { this.mobileno = mobileno; }
    public String getEori() { return eori; }
    public void setEori(String eori) { this.eori = eori; }
    public String getFillingtype() { return fillingtype; }
    public void setFillingtype(String fillingtype) { this.fillingtype = fillingtype; }
    public String getCrUsr() { return crUsr; }
    public void setCrUsr(String crUsr) { this.crUsr = crUsr; }
    public Date getCrDate() { return crDate; }
    public void setCrDate(Date crDate) { this.crDate = crDate; }
    public String getChgUsr() { return chgUsr; }
    public void setChgUsr(String chgUsr) { this.chgUsr = chgUsr; }
    public Date getChgDate() { return chgDate; }
    public void setChgDate(Date chgDate) { this.chgDate = chgDate; }
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getCountryId() { return countryId; }
    public void setCountryId(String countryId) { this.countryId = countryId; }
    public String getBillAddressee() { return billAddressee; }
    public void setBillAddressee(String billAddressee) { this.billAddressee = billAddressee; }
    public String getBillPhone() { return billPhone; }
    public void setBillPhone(String billPhone) { this.billPhone = billPhone; }
    public String getBillEmail() { return billEmail; }
    public void setBillEmail(String billEmail) { this.billEmail = billEmail; }
    public String getBillmobileNo() { return billmobileNo; }
    public void setBillmobileNo(String billmobileNo) { this.billmobileNo = billmobileNo; }
    public String getBillAddress1() { return billAddress1; }
    public void setBillAddress1(String billAddress1) { this.billAddress1 = billAddress1; }
    public String getBillAddress2() { return billAddress2; }
    public void setBillAddress2(String billAddress2) { this.billAddress2 = billAddress2; }
    public String getBillCity() { return billCity; }
    public void setBillCity(String billCity) { this.billCity = billCity; }
    public String getBillZipCode() { return billZipCode; }
    public void setBillZipCode(String billZipCode) { this.billZipCode = billZipCode; }
    public String getBillCountry() { return billCountry; }
    public void setBillCountry(String billCountry) { this.billCountry = billCountry; }
}
