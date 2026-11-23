package com.svm.ecom.master.repository.masters;

/**
 * Billing columns from customermaster (Excel Billing sheet).
 */
public class BillingRow {

    private String custCode;
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
