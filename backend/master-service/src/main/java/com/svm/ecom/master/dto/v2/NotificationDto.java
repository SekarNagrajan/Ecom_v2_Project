package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Notification contact. Same documentation style as client request standard.
 */
@Schema(name = "Notification", description = "Represents a notification contact for the customer (e.g. for alerts or documents).",
        example = "{\"notificationType\":\"Email\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"fax\":\"+1 555-0199\",\"mobile\":\"+1 555-0198\"}")
public class NotificationDto {

    @Schema(description = "The type of notification (e.g. Email, Fax, SMS).", example = "Email")
    private String notificationType;
    @Schema(description = "Full name of the notification contact.", example = "John Doe")
    private String name;
    @Schema(description = "Email address for notifications.", example = "john.doe@example.com")
    private String email;
    @Schema(description = "Fax number for notifications.", example = "+1 555-0199")
    private String fax;
    @Schema(description = "Mobile number for notifications.", example = "+1 555-0198")
    private String mobile;

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
