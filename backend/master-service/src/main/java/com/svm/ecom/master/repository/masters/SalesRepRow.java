package com.svm.ecom.master.repository.masters;

import java.util.Date;

/**
 * Row from CustomerSalesRep + salesrep (Excel Sales Rep sheet).
 */
public class SalesRepRow {

    private String custcode;
    private String salesRepCode;
    private Date validFrom;
    private Date validTill;
    private String name;
    private String city;

    public String getCustcode() { return custcode; }
    public void setCustcode(String custcode) { this.custcode = custcode; }
    public String getSalesRepCode() { return salesRepCode; }
    public void setSalesRepCode(String salesRepCode) { this.salesRepCode = salesRepCode; }
    public Date getValidFrom() { return validFrom; }
    public void setValidFrom(Date validFrom) { this.validFrom = validFrom; }
    public Date getValidTill() { return validTill; }
    public void setValidTill(Date validTill) { this.validTill = validTill; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
