package com.svm.ecom.master.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contact for v2 Customer Master API. Matches client request standard.
 */
@Schema(name = "CustomerContact", description = "Represents a specific contact person within the customer organization. Required: id, name, email. id is the webid from customercontacts.",
        example = "{\"id\":\"Denis.Schramm@lidl.com\",\"name\":\"Mr.Denis Schramm\",\"phone\":\"+0049-1522-1523325\",\"email\":\"Denis.Schramm@lidl.com\",\"designation\":null,\"contactType\":\"Online\"}")
public class CustomerContactDto {

    @Schema(description = "Unique web ID for the contact.", example = "Denis.Schramm@lidl.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(description = "Full name of the contact person.", example = "Mr.Denis Schramm", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "Direct phone number for the contact.", example = "+0049-1522-1523325")
    private String phone;
    @Schema(description = "Email address of the contact.", example = "Denis.Schramm@lidl.com", requiredMode = Schema.RequiredMode.REQUIRED, format = "email")
    private String email;
    @Schema(description = "Job title or designation of the contact person.", example = "Logistics Manager")
    private String designation;
    @Schema(description = "Type of contact (e.g. Online).", example = "Online")
    private String contactType;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getContactType() { return contactType; }
    public void setContactType(String contactType) { this.contactType = contactType; }
}
