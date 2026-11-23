package com.svm.ecom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Nested details for a parent error (client request standard – V2 APIs).
 * Schema name: NestedErrorMessage.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "NestedErrorMessage", description = "Nested details about a parent error (e.g. Customer API validation). Required: detail.",
        example = "{\"detail\":\"The email address does not match the required format.\",\"pointer\":\"#/email\",\"code\":\"INVALID_EMAIL_FORMAT\"}")
public class NestedErrorMessage {

    @Schema(description = "Granular description of the error (e.g. customer field validation: email, customerCode, address).", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detail;

    @Schema(description = "JSON pointer to the Customer/Master request property that caused the error (e.g. #/email, #/address/lines).")
    private String pointer;

    @Schema(description = "The name of the query or path parameter that is the source of the error")
    private String parameter;

    @Schema(description = "The name of the header that is the source of the error")
    private String header;

    @Schema(description = "Provider-specific error code (e.g. INVALID_EMAIL_FORMAT, INVALID_CUSTOMER_CODE).")
    private String code;

    public NestedErrorMessage() {}

    public NestedErrorMessage(String detail, String pointer, String parameter, String header, String code) {
        this.detail = detail;
        this.pointer = pointer;
        this.parameter = parameter;
        this.header = header;
        this.code = code;
    }

    public static NestedErrorMessage of(String detail, String pointer, String code) {
        return new NestedErrorMessage(detail, pointer, null, null, code);
    }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getPointer() { return pointer; }
    public void setPointer(String pointer) { this.pointer = pointer; }
    public String getParameter() { return parameter; }
    public void setParameter(String parameter) { this.parameter = parameter; }
    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
