package com.svm.ecom.master.repository.masters;

/**
 * Row from customers_EnsDetails (Excel ENS sheet).
 */
public class EnsRow {

    private String custcode;
    private String ensname;
    private String ensemail;
    private String enseori;
    private Boolean ensdefaultdec;

    public String getCustcode() { return custcode; }
    public void setCustcode(String custcode) { this.custcode = custcode; }
    public String getEnsname() { return ensname; }
    public void setEnsname(String ensname) { this.ensname = ensname; }
    public String getEnsemail() { return ensemail; }
    public void setEnsemail(String ensemail) { this.ensemail = ensemail; }
    public String getEnseori() { return enseori; }
    public void setEnseori(String enseori) { this.enseori = enseori; }
    public Boolean getEnsdefaultdec() { return ensdefaultdec; }
    public void setEnsdefaultdec(Boolean ensdefaultdec) { this.ensdefaultdec = ensdefaultdec; }
}
