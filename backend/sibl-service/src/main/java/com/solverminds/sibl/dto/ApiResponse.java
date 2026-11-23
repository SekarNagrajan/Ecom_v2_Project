package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solverminds.sibl.util.DtoUtil;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import com.svm.ecom.docs.ModelDocs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Generic API Response wrapper for SIBL Service endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.ApiResponse.DESC)
public class ApiResponse<T> {
    
    @Schema(description = ModelDocs.ApiResponse.TYPE, example = ModelDocs.ApiResponse.EX_TYPE)
    private String type;
    
    @Schema(description = ModelDocs.Common.CUSTOMER_CODE, example = ModelDocs.Common.EX_CUSTOMER)
    private String customerCode;
    
    @Schema(description = ModelDocs.ApiResponse.DATA)
    private List<T> data;
    
    @Schema(type = "string", format = "date-time", description = ModelDocs.ApiResponse.TIMESTAMP, example = ModelDocs.Common.EX_DATETIME_TZ)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime timestamp;
    
    @Schema(description = ModelDocs.ApiResponse.TOTAL_RECORDS, example = ModelDocs.ApiResponse.EX_TOTAL_RECORDS)
    private Integer totalRecords;
    
    @Schema(description = ModelDocs.ApiResponse.SUCCESS, example = ModelDocs.ApiResponse.EX_SUCCESS)
    private Boolean success;
    
    public ApiResponse(String type, String customerCode, List<T> data) {
        this.type = type;
        this.customerCode = customerCode;
        this.data = data;
        this.timestamp = LocalDateTime.now().withNano(0);
        this.totalRecords = data != null ? data.size() : 0;
        this.success = true;
    }
    
    // Constructor without customerCode for responses that shouldn't include it
    public ApiResponse(String type, List<T> data) {
        this.type = type;
        this.data = data;
        this.timestamp = LocalDateTime.now().withNano(0);
        this.totalRecords = data != null ? data.size() : 0;
        this.success = true;
    }

    /** Returns type; never null in JSON (empty string if null). */
    public String getType() { return DtoUtil.emptyIfNull(type); }
    /** Returns customerCode; never null in JSON (empty string if null). */
    public String getCustomerCode() { return DtoUtil.emptyIfNull(customerCode); }
    /** Returns data; never null in JSON (empty list if null). */
    public List<T> getData() { return DtoUtil.emptyListIfNull(data); }
}
