package com.svm.ecom.master.repository.masters;

/**
 * Row from CustomerNotifications (Excel Notifications sheet).
 */
public class NotificationRow {

    private String custcode;
    private String notificationType;
    private String name;
    private String email;
    private String fax;
    private String mobile;

    public String getCustcode() { return custcode; }
    public void setCustcode(String custcode) { this.custcode = custcode; }
    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFax() { return fax; }
    public void setFax(String fax) { this.fax = fax; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
}
