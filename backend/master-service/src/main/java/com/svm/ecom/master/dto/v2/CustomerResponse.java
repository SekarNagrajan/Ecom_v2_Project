package com.svm.ecom.master.dto.v2;

import com.svm.ecom.common.enums.CustomerType;
import com.svm.ecom.common.enums.EnsFillingType;
import com.svm.ecom.master.enums.CustomerStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Customer response for GET /api/v2/master/customers.
 * Schema name and documentation match client request standard.
 */
@Schema(name = "Customer", description = "Represents a customer entity. Required properties: customerCode, address, email, customerTypes, status. Example based on live API response (LIDL EMB KG) with all keys having valid values.",
        example = "{\"customerCode\":\"2022000001\",\"customerName\":\"LIDL EMB KG\",\"status\":\"VAL\",\"address\":{\"lines\":[\"Floor 4, Unit 12\",\"123 Innovation Drive\"],\"locality\":\"Neckarsulm\",\"countryCode\":\"DE\"},\"telephone\":\"+49-7132-942220\",\"email\":\"test@lidl.com\",\"customerTypes\":[\"BP\"],\"activeFrom\":\"2022-06-08T00:00:00Z\",\"typeOfPerson\":\"Legal\",\"mobile\":\"+49-7132-942220\",\"eori\":\"DE6794610\",\"ensFilingType\":\"N\",\"createdUser\":\"KARACA.SEB\",\"createdDate\":\"2022-06-09T00:00:00Z\",\"updatedUser\":\"MEYER.BJO\",\"updatedDate\":\"2026-01-22T00:00:00Z\",\"billing\":{\"name\":\"LIDL EMB KG\",\"phone\":\"+49-7132-942220\",\"email\":\"test@lidl.com\",\"mobile\":\"+49-7132-942220\",\"lines\":[\"Floor 4, Unit 12\",\"123 Innovation Drive\"],\"locality\":\"Neckarsulm\",\"countryCode\":\"DE\"},\"financialInformation\":[{\"creditType\":\"TotalCredit\",\"currency\":\"USD\",\"validFrom\":\"2023-01-02T18:30:00Z\",\"validTo\":\"2031-12-30T18:30:00Z\",\"creditDays\":30,\"creditLimit\":1.0}],\"notifications\":[{\"notificationType\":\"Email\",\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"fax\":\"+1-555-0199\",\"mobile\":\"+1-555-0198\"}],\"contacts\":[{\"id\":\"Denis.Schramm@lidl.com\",\"name\":\"Mr.Denis Schramm\",\"phone\":\"+0049-1522-1523325\",\"email\":\"Denis.Schramm@lidl.com\",\"designation\":\"Logistics Manager\",\"contactType\":\"Online\"},{\"id\":\"ole.gaethgens@tailwind-shipping.com\",\"name\":\"Ole Gäthgens\",\"phone\":\"+49-7132-94285941\",\"email\":\"ole.gaethgens@tailwind-shipping.com\",\"designation\":\"Logistics Manager\",\"contactType\":\"Online\"}],\"salesRep\":[{\"code\":\"SR001\",\"name\":\"Jane Smith\",\"city\":\"Neckarsulm\",\"activeFrom\":\"2022-06-08T00:00:00Z\",\"activeTill\":\"2026-12-31T00:00:00Z\"}],\"ens\":[{\"declarantName\":\"COMMODITY SUPPLIES AG\",\"email\":\"Lidl@commodity.ch\",\"eoriId\":\"DE1172522\",\"defaultDeclarant\":true}]}")
public class CustomerResponse {

    @Schema(description = "Unique code identifying the customer.", example = "2022000001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerCode;
    @Schema(description = "Name of the customer.", example = "LIDL EMB KG")
    private String customerName;
    @Schema(description = "The customer's status. API returns status code (e.g. VAL for Validated).", example = "VAL", requiredMode = Schema.RequiredMode.REQUIRED, implementation = CustomerStatus.class)
    private CustomerStatus status;
    @Schema(description = "Customer address. A physical postal address capable of supporting international formats.", requiredMode = Schema.RequiredMode.REQUIRED)
    private AddressDto address;
    @Schema(description = "The telephone number of the customer.", example = "+49-7132-942220")
    private String telephone;
    @Schema(description = "The general email address of the customer.", example = "test@lidl.com", requiredMode = Schema.RequiredMode.REQUIRED, format = "email")
    private String email;
    @Schema(description = "List of customer types the customer can act as. API returns codes: AP – Agreement Party, BP – Booking Party, BY – Buyer, CN – Consignee, DL – DropOff Location, FW – Freight Forwarder, N1 – Notify Party 1, N2 – Notify Party 2, NT – Notify, PL – PickUp Location, SH – Shipper, SL – Seller.", example = "[\"BP\"]", requiredMode = Schema.RequiredMode.REQUIRED, implementation = CustomerType.class)
    private List<CustomerType> customerTypes;
    @Schema(description = "Active from date timestamp.", format = "date-time", example = "2022-06-08T00:00:00Z")
    private String activeFrom;
    @Schema(description = "Type of person (e.g. natural, legal).", example = "Legal")
    private String typeOfPerson;
    @Schema(description = "Mobile number of the customer.", example = "+49-7132-942220")
    private String mobile;
    @Schema(description = "EORI number of the customer.", example = "DE6794610")
    private String eori;
    @Schema(description = "ENS filing type: S (Single), P (Partial), N (N/A). Same enum as booking.", example = "N", implementation = EnsFillingType.class)
    private EnsFillingType ensFilingType;
    @Schema(description = "User who created the record.", example = "KARACA.SEB")
    private String createdUser;
    @Schema(description = "Created date timestamp.", format = "date-time", example = "2022-06-09T00:00:00Z")
    private String createdDate;
    @Schema(description = "User who last updated the record.", example = "MEYER.BJO")
    private String updatedUser;
    @Schema(description = "Updated date timestamp.", format = "date-time", example = "2026-01-22T00:00:00Z")
    private String updatedDate;
    @Schema(description = "Billing address and contact for the customer.")
    private BillingDto billing;
    @Schema(description = "List of financial information (credit type, currency, validity, credit days/limit).")
    private List<FinancialInfoDto> financialInformation;
    @Schema(description = "List of notification contacts for the customer.")
    private List<NotificationDto> notifications;
    @Schema(description = "List of contacts associated with this customer.")
    private List<CustomerContactDto> contacts;
    @Schema(description = "List of sales representatives assigned to this customer.")
    private List<SalesRepDto> salesRep;
    @Schema(description = "List of ENS (Export Notification System) declarant details.")
    private List<EnsDto> ens;

    public String getCustomerCode() { return customerCode; }
    public void setCustomerCode(String customerCode) { this.customerCode = customerCode; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public CustomerStatus getStatus() { return status; }
    public void setStatus(CustomerStatus status) { this.status = status; }
    public AddressDto getAddress() { return address; }
    public void setAddress(AddressDto address) { this.address = address; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<CustomerType> getCustomerTypes() { return customerTypes; }
    public void setCustomerTypes(List<CustomerType> customerTypes) { this.customerTypes = customerTypes; }
    public String getActiveFrom() { return activeFrom; }
    public void setActiveFrom(String activeFrom) { this.activeFrom = activeFrom; }
    public String getTypeOfPerson() { return typeOfPerson; }
    public void setTypeOfPerson(String typeOfPerson) { this.typeOfPerson = typeOfPerson; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getEori() { return eori; }
    public void setEori(String eori) { this.eori = eori; }
    public EnsFillingType getEnsFilingType() { return ensFilingType; }
    public void setEnsFilingType(EnsFillingType ensFilingType) { this.ensFilingType = ensFilingType; }
    public String getCreatedUser() { return createdUser; }
    public void setCreatedUser(String createdUser) { this.createdUser = createdUser; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getUpdatedUser() { return updatedUser; }
    public void setUpdatedUser(String updatedUser) { this.updatedUser = updatedUser; }
    public String getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
    public BillingDto getBilling() { return billing; }
    public void setBilling(BillingDto billing) { this.billing = billing; }
    public List<FinancialInfoDto> getFinancialInformation() { return financialInformation; }
    public void setFinancialInformation(List<FinancialInfoDto> financialInformation) { this.financialInformation = financialInformation; }
    public List<NotificationDto> getNotifications() { return notifications; }
    public void setNotifications(List<NotificationDto> notifications) { this.notifications = notifications; }
    public List<CustomerContactDto> getContacts() { return contacts; }
    public void setContacts(List<CustomerContactDto> contacts) { this.contacts = contacts; }
    public List<SalesRepDto> getSalesRep() { return salesRep; }
    public void setSalesRep(List<SalesRepDto> salesRep) { this.salesRep = salesRep; }
    public List<EnsDto> getEns() { return ens; }
    public void setEns(List<EnsDto> ens) { this.ens = ens; }
}
