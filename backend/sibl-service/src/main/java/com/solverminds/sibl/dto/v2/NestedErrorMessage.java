package com.solverminds.sibl.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Nested details about a parent error")
public class NestedErrorMessage {

    @Schema(description = "A granular description of the specific error",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String detail;

    @Schema(description = "A pointer to a specific request body property that is the source of the error")
    private String pointer;

    @Schema(description = "The name of the query or path parameter that is the source of the error")
    private String parameter;

    @Schema(description = "The name of the header that is the source of the error")
    private String header;

    @Schema(description = "A string containing additional provider-specific codes to identify the error context")
    private String code;

    public NestedErrorMessage() {}

    public NestedErrorMessage(String detail, String pointer, String code) {
        this.detail = detail;
        this.pointer = pointer;
        this.code = code;
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
