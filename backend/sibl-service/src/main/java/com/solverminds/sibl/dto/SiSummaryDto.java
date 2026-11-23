package com.solverminds.sibl.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.svm.ecom.common.enums.BlStatus;
import com.svm.ecom.common.enums.BookingStatus;
import com.svm.ecom.common.enums.SiStatus;
import com.svm.ecom.constants.DateFormatConstants;
import com.svm.ecom.docs.ModelDocs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * DTO for SI Summary response containing results from usp_ecomSISummaryNew stored procedure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.SiSummaryDto.DESC)
public class SiSummaryDto {
    
    @Schema(description = ModelDocs.SiSummaryDto.BOOK_NO, example = ModelDocs.SiSummaryDto.EX_BOOK_NO)
    private String bookNo;
    
    @Schema(description = ModelDocs.SiSummaryDto.DOC_ID, example = ModelDocs.SiSummaryDto.EX_DOC_ID)
    private String docId;
    
    @Schema(description = ModelDocs.SiSummaryDto.SI_STATUS, example = ModelDocs.SiSummaryDto.EX_SI_STATUS)
    private SiStatus siStatus;
    
    @Schema(description = ModelDocs.SiSummaryDto.BOOK_SOURCE, example = ModelDocs.SiSummaryDto.EX_BOOK_SOURCE)
    private String bookSource;
    
    @Schema(description = ModelDocs.SiSummaryDto.BL_NO, example = ModelDocs.SiSummaryDto.EX_BL_NO)
    private String blNo;
    
    @Schema(description = ModelDocs.SiSummaryDto.BL_STATUS, example = ModelDocs.SiSummaryDto.EX_BL_STATUS)
    private BlStatus blStatus;
    
    @Schema(description = ModelDocs.SiSummaryDto.LOAD_PORT_ID, example = ModelDocs.SiSummaryDto.EX_LOAD_PORT)
    private String loadPortId;
    
    @Schema(description = ModelDocs.SiSummaryDto.DISCHARGE_PORT_ID, example = ModelDocs.SiSummaryDto.EX_DISCHARGE_PORT)
    private String dischargePortId;
    
    @Schema(description = "POL departure in load port local time (ISO 8601 with offset)", example = "2023-11-23T15:30:00+08:00")
    private OffsetDateTime polAt;
    
    @Schema(description = ModelDocs.SiSummaryDto.CREATED_SI, example = ModelDocs.SiSummaryDto.EX_CREATED_SI)
    private String createdSi;
    
    @Schema(description = ModelDocs.SiSummaryDto.REQUESTED_SI, example = ModelDocs.SiSummaryDto.EX_REQUESTED_SI)
    private String requestedSi;
    
    @Schema(description = ModelDocs.SiSummaryDto.BK_STATUS, example = ModelDocs.SiSummaryDto.EX_BK_STATUS)
    private BookingStatus bkStatus;
    
    @Schema(description = ModelDocs.SiSummaryDto.AGENCY_REF_NO, example = ModelDocs.SiSummaryDto.EX_AGENCY_REF)
    private String agencyRefNo;
    
    @Schema(description = ModelDocs.SiSummaryDto.APP_VERSION, example = ModelDocs.SiSummaryDto.EX_APP_VERSION)
    private String appVersion;
    
    @Schema(description = ModelDocs.SiSummaryDto.ORIGIN_PORT_ID, example = ModelDocs.SiSummaryDto.EX_LOAD_PORT)
    private String originPortId;
    
    @Schema(description = ModelDocs.SiSummaryDto.FINAL_PORT_ID, example = ModelDocs.SiSummaryDto.EX_DISCHARGE_PORT)
    private String finalPortId;
    
    @Schema(description = ModelDocs.SiSummaryDto.IS_SI_LOCKED, example = ModelDocs.SiSummaryDto.EX_IS_SI_LOCKED)
    private Boolean isSiLocked;
    
@Schema(type = "string", format = "date-time", description = ModelDocs.SiSummaryDto.DATE_CR, example = ModelDocs.Common.EX_DATETIME_TZ)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime dateCr;

    @Schema(type = "string", format = "date-time", description = ModelDocs.SiSummaryDto.DATE_UP, example = ModelDocs.Common.EX_DATETIME_TZ)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime dateUp;
}
