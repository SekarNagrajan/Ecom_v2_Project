package com.solverminds.sibl.dto.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO mapping the JSON response from the external BL print API
 * ({base-url}/blprint?blNo=...&printType=D&userName=...).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlPrintResponse {

    private Boolean success;
    private String message;
    private String blNo;
    private String content;

    public BlPrintResponse() {}

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBlNo() {
        return blNo;
    }

    public void setBlNo(String blNo) {
        this.blNo = blNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
