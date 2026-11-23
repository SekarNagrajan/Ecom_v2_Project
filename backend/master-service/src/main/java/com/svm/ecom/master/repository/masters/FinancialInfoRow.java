package com.svm.ecom.master.repository.masters;

import java.util.Date;

/**
 * Row from Customerfinalcialinformation (Excel Financial Information sheet).
 */
public class FinancialInfoRow {

    private String custcode;
    private String type;
    private String currency;
    private Date validityfrom;
    private Date validityto;
    private Integer creditperiod;
    private Double creditLimit;

    public String getCustcode() { return custcode; }
    public void setCustcode(String custcode) { this.custcode = custcode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Date getValidityfrom() { return validityfrom; }
    public void setValidityfrom(Date validityfrom) { this.validityfrom = validityfrom; }
    public Date getValidityto() { return validityto; }
    public void setValidityto(Date validityto) { this.validityto = validityto; }
    public Integer getCreditperiod() { return creditperiod; }
    public void setCreditperiod(Integer creditperiod) { this.creditperiod = creditperiod; }
    public Double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(Double creditLimit) { this.creditLimit = creditLimit; }
}
