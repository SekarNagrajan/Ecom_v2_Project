package com.solverminds.booking.dto.v2;

import com.svm.ecom.common.enums.CustomerType;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Details about a booking customer for V2 API.
 */
@Schema(name = "BookingCustomer", description = "Details about a booking customer")
public class BookingCustomerV2 {

    @NotNull
    @Schema(description = "Customer type", example = "BP")
    private CustomerType type;

    @NotBlank
    @Schema(description = "Customer code", example = "2024003615")
    private String code;

    @NotBlank
    @Schema(description = "Customer name", example = "SND SQUARE INTERNATIONAL LTD.")
    private String name;

    @NotNull
    @Valid
    @Schema(description = "Customer address")
    private AddressV2 address;

    @Schema(description = "Telephone number", example = "86-38-1234567")
    @Size(max = 25, message = "telephone must not exceed 25 characters")
    private String telephone;

    @Schema(description = "Email address", example = "john@doe.com")
    @Size(max = 100, message = "email must not exceed 100 characters")
    private String email;

    public BookingCustomerV2() {
    }

    public CustomerType getType() {
        return type;
    }

    public void setType(CustomerType type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressV2 getAddress() {
        return address;
    }

    public void setAddress(AddressV2 address) {
        this.address = address;
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

