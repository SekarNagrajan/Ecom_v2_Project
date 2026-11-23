package com.svm.ecom.master.repository.masters;

/**
 * Row projection from customercontacts for v2 customer list.
 */
public class CustomerContactRow {

    private String custcode;
    private String contactName;
    private String email;
    private String phone;
    private String designation;
    private String mobile;
    private String contactType;
    private String webid;

    public String getCustcode() { return custcode; }
    public void setCustcode(String custcode) { this.custcode = custcode; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }
    public String getWebid() { return webid; }
    public void setWebid(String webid) { this.webid = webid; }
}
