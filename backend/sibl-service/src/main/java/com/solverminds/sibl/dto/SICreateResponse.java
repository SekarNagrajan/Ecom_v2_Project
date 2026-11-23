package com.solverminds.sibl.dto;

import com.solverminds.sibl.util.DtoUtil;
import com.svm.ecom.common.enums.SiStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SICreateResponse", description = "Response after SI creation")
public class SICreateResponse {
    @Schema(description = "Online SI number", example = "SIREQ123456")
    private String onlineSINo;
    @Schema(description = "SI status - D (Draft) or S (Submitted) or A (Accepted) or V (Declined)", example = "D")
    private SiStatus status;
    @Schema(description = "Response message", example = "SI created successfully")
    private String message;

    public SICreateResponse() {}
    public SICreateResponse(String onlineSINo, SiStatus status, String message) {
        this.onlineSINo = onlineSINo;
        this.status = status;
        this.message = message;
    }

    public String getOnlineSINo() { return DtoUtil.emptyIfNull(onlineSINo); }
    public void setOnlineSINo(String onlineSINo) { this.onlineSINo = onlineSINo; }
    public SiStatus getStatus() { return status; }
    public void setStatus(SiStatus status) { this.status = status; }
    public String getMessage() { return DtoUtil.emptyIfNull(message); }
    public void setMessage(String message) { this.message = message; }
}
