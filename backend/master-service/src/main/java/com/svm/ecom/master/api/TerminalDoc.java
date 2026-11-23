package com.svm.ecom.master.api;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI documentation schema for terminal data
 * Used only for API documentation purposes
 */
@Schema(description = "Terminal master data")
public class TerminalDoc {

    @Schema(description = "Terminal Code", example = "INMUNACT01", required = true)
    private String terminalCode;

    @Schema(description = "Terminal Name", example = "ADANI CONTAINER TERMINAL", required = true)
    private String terminalName;

    // Constructors
    public TerminalDoc() {
    }

    public TerminalDoc(String terminalCode, String terminalName) {
        this.terminalCode = terminalCode;
        this.terminalName = terminalName;
    }

    // Getters and setters
    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

}
