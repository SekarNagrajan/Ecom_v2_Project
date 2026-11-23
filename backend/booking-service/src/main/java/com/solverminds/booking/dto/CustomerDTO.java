package com.solverminds.booking.dto;

import com.solverminds.booking.dto.validation.ValidCustomerType;
import com.solverminds.booking.dto.validation.ValidCustomerMasterData;
import com.svm.ecom.common.enums.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;

/**
 * Customer party details such as Booking Party (BP), Agreement Party (AP), Buyer (BY), Consignee (CN), 
 * Freight Forwarder (FW), Notify Party 1 (N1), Notify (NT), Shipper (SH), or Seller (SL).
 */
@Schema(description = "Customer party details including all party types (BP, AP, BY, CN, FW, N1, NT, SH, SL)")
@ValidCustomerType
@ValidCustomerMasterData
public class CustomerDTO {

    @NotNull(message = "customerType is required")
    @Schema(description = "Customer role code", 
            example = "BP",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private CustomerType customerType;

    @NotBlank(message = "Field must be provided (non-empty).")
    @Size(max = 20, message = "customerCode must not exceed 20 characters")
    @Pattern(regexp="^[0-9A-Za-z_-]*$", message="customerCode must be alphanumeric with optional _ or -")
    @Schema(description = "Customer code", example = "20001374")
    private String customerCode;

    @NotBlank(message = "Field must be provided (non-empty).")
    @Pattern(regexp="^[A-Z]{2}$", message="countryId must be ISO 3166-1 alpha-2 (e.g., IN, CN)")
    @Schema(description = "Country ID/code", example = "CN")
    private String countryId;

    @NotBlank(message = "Field must be provided (non-empty).")
    @Size(max = 150, message = "customerName must not exceed 150 characters")
    @Schema(description = "Customer name", example = "ECOM TESTING")
    private String customerName;

    @NotBlank(message = "Field must be provided (non-empty).")
    @Size(max = 150, message = "address must not exceed 150 characters")
    @Schema(description = "Primary address", example = "513B, Building A,Free Trade Financial Center")
    private String address;

    @Schema(description = "Additional address line", example = "Xiamen Area of China")
    @Size(max = 150, message = "address1 must not exceed 150 characters")
    private String address1;

    @NotBlank(message = "Field must be provided (non-empty).")
    @Size(max=120)
    @Schema(description = "City", example = "Beijiao, China")
    private String city;

    @Size(max = 15, message = "zipCode must not exceed 15 characters")
    @Pattern(regexp="^[0-9A-Za-z\\-\\s]*$", message="zipCode may contain letters, numbers, spaces, and dashes")
    @Schema(description = "ZIP/postal code", example = "603103")
    private String zipCode;

    @Size(max = 25, message = "telephone must not exceed 25 characters")
    @Pattern(regexp="^\\+?[0-9]{1,4}[\\-\\s]?[0-9]{2,4}[\\-\\s]?[0-9]{4,10}$", message="Phone number must be in correct format")
    @Schema(description = "Telephone number", example = "86-38-00138000")
    private String telephone;

    @Email(message="Contact email must be in valid format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Contact email must be in valid format")
    @Size(max = 100, message = "email must not exceed 100 characters")
    @Schema(description = "Email address", example = "email@solverminds.com")
    private String email;

    public CustomerDTO() {
    }

    public CustomerDTO(CustomerType customerType, String customerCode, String countryId, String customerName,
                      String address, String address1, String city, String zipCode, String telephone, String email) {
        this.customerType = customerType;
        this.customerCode = customerCode;
        this.countryId = countryId;
        this.customerName = customerName;
        this.address = address;
        this.address1 = address1;
        this.city = city;
        this.zipCode = zipCode;
        this.telephone = telephone;
        this.email = email;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
