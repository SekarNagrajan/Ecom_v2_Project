package com.solverminds.ecom.register.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.constants.DateFormatConstants;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ecomuserdetails")
@NoArgsConstructor
@AllArgsConstructor
public class EcomUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "EmailID", nullable = false, length = 150)
    private String emailID;

    @Column(name = "Password", nullable = false, columnDefinition = "varchar(max)")
    private String password;

    @Column(name = "userRole", nullable = false, columnDefinition = "char(1)")
    private String userRole;

    @Column(name = "CustomerCode", nullable = false, length = 20)
    private String customerCode;

    @Column(name = "Status", nullable = false, columnDefinition = "char(1)")
    private String status;

    @Column(name = "AcceptedDate")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime acceptedDate;

    @Column(name = "MenuId", nullable = false, length = 150)
    private String menuId;

    @Column(name = "ccmailid", nullable = false, length = 500)
    private String ccmailid;

    @Column(name = "apptoken", length = 100)
    private String apptoken;

    @Column(name = "apptokenCreatedAt")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime apptokenCreatedAt;

    @Column(name = "preferredTimezone", nullable = false, length = 150)
    private String preferredTimezone;

    @Column(name = "lastpwdup")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime lastpwdup;

    @Column(name = "logindate")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime logindate;

    @Column(name = "logoutdate")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime logoutdate;

    @Column(name = "WrgPassAtmpt")
    private Integer wrgPassAtmpt;

    @Column(name = "LockedStatus", nullable = false, columnDefinition = "char(1)")
    private String lockedStatus;

    @Column(name = "LockedTime")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime lockedTime;

    /**
     * Checks if user is active - accepts both 'A' and '1' as active status
     */
    public boolean isActive() {
        return "A".equals(status) || "1".equals(status);
    }

    /**
     * Provisional quick-registration user (dummy customer code, not yet fully provisioned).
     */
    public boolean isDummy() {
        return "D".equals(status);
    }

    /**
     * Whether credentials may be used for e-com login (active or provisional dummy).
     */
    public boolean isLoginAllowed() {
        return isActive() || isDummy();
    }

    /**
     * Checks if user is locked
     */
    public boolean isLocked() {
        return "Y".equals(lockedStatus);
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmailID() { return emailID; }
    public void setEmailID(String emailID) { this.emailID = emailID; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAcceptedDate() { return acceptedDate; }
    public void setAcceptedDate(LocalDateTime acceptedDate) { this.acceptedDate = acceptedDate; }

    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }

    public String getCcmailid() { return ccmailid; }
    public void setCcmailid(String ccmailid) { this.ccmailid = ccmailid; }

    public String getApptoken() { return apptoken; }
    public void setApptoken(String apptoken) { this.apptoken = apptoken; }

    public LocalDateTime getApptokenCreatedAt() { return apptokenCreatedAt; }
    public void setApptokenCreatedAt(LocalDateTime apptokenCreatedAt) { this.apptokenCreatedAt = apptokenCreatedAt; }

    public String getPreferredTimezone() { return preferredTimezone; }
    public void setPreferredTimezone(String preferredTimezone) { this.preferredTimezone = preferredTimezone; }

    public LocalDateTime getLastpwdup() { return lastpwdup; }
    public void setLastpwdup(LocalDateTime lastpwdup) { this.lastpwdup = lastpwdup; }

    public LocalDateTime getLogindate() { return logindate; }
    public void setLogindate(LocalDateTime logindate) { this.logindate = logindate; }

    public LocalDateTime getLogoutdate() { return logoutdate; }
    public void setLogoutdate(LocalDateTime logoutdate) { this.logoutdate = logoutdate; }

    public Integer getWrgPassAtmpt() { return wrgPassAtmpt; }
    public void setWrgPassAtmpt(Integer wrgPassAtmpt) { this.wrgPassAtmpt = wrgPassAtmpt; }

    public String getLockedStatus() { return lockedStatus; }
    public void setLockedStatus(String lockedStatus) { this.lockedStatus = lockedStatus; }

    public LocalDateTime getLockedTime() { return lockedTime; }
    public void setLockedTime(LocalDateTime lockedTime) { this.lockedTime = lockedTime; }
}
