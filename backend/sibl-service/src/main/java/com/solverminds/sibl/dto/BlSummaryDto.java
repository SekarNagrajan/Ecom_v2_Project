package com.solverminds.sibl.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.svm.ecom.common.enums.BlStatus;
import com.svm.ecom.common.enums.EcomPrintStatus;
import com.svm.ecom.constants.DateFormatConstants;
import com.svm.ecom.docs.ModelDocs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * DTO for BL Summary response from usp_EcomBLSummary stored procedure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = ModelDocs.BlSummaryDto.DESC)
public class BlSummaryDto {
    
    @Schema(description = ModelDocs.BlSummaryDto.BOOK_NO, example = ModelDocs.BlSummaryDto.EX_BOOK_NO)
    private String bookNo;
    
    @Schema(description = ModelDocs.BlSummaryDto.REFVAL, example = ModelDocs.BlSummaryDto.EX_REFVAL)
    private String refval;
    
    @Schema(description = ModelDocs.BlSummaryDto.AGENCY_REF_NO, example = ModelDocs.BlSummaryDto.EX_AGENCY_REF)
    private String agencyrefno;
    
    @Schema(description = ModelDocs.BlSummaryDto.BL_NO, example = ModelDocs.BlSummaryDto.EX_BL_NO)
    private String blNo;
    
    @Schema(description = ModelDocs.BlSummaryDto.ORIGIN_PORT_ID, example = ModelDocs.BlSummaryDto.EX_ORIGIN_PORT)
    private String originPortId;
    
    @Schema(description = ModelDocs.BlSummaryDto.LOAD_PORT_ID, example = ModelDocs.BlSummaryDto.EX_LOAD_PORT)
    private String loadportid;
    
    @Schema(description = ModelDocs.BlSummaryDto.DISCHARGE_PORT_ID, example = ModelDocs.BlSummaryDto.EX_DISCHARGE_PORT)
    private String dischargeportid;
    
    @Schema(description = ModelDocs.BlSummaryDto.FINAL_PORT_ID, example = ModelDocs.BlSummaryDto.EX_FINAL_PORT)
    private String finalPortId;
    
    @Schema(description = "POL departure in load port local time (ISO 8601 with offset)", example = "2023-11-23T15:30:00+08:00")
    private OffsetDateTime polat;
    
    @Schema(description = ModelDocs.BlSummaryDto.STATUS, implementation = BlStatus.class)
    private BlStatus status;
    
    @Schema(hidden = true)
    @JsonIgnore
    private Boolean isFCN;
    
    @Schema(description = ModelDocs.BlSummaryDto.DOCUMENTAL_AGENCY_ID, example = ModelDocs.BlSummaryDto.EX_DOC_AGENCY_ID)
    private String documentalAgencyId;
    
    @Schema(hidden = true)
    private String blType;
    
    @Schema(hidden = true)
    @JsonIgnore
    private String systembltype;
    
    @Schema(type = "string", format = "date-time", description = ModelDocs.BlSummaryDto.DATE_CR, example = ModelDocs.Common.EX_DATETIME_TZ)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime datecr;
    
    @Schema(type = "string", format = "date-time", description = ModelDocs.BlSummaryDto.DATE_UP, example = ModelDocs.Common.EX_DATETIME_TZ)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime dateup;
    
    @Schema(hidden = true)
    @JsonIgnore
    private String shipmentStatus;
    
    @Schema(type = "string", format = "date-time", description = ModelDocs.BlSummaryDto.CONFIRMED_DATE, example = ModelDocs.Common.EX_DATETIME_TZ)
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime confirmeddate;
    
    @Schema(description = ModelDocs.BlSummaryDto.ECOM_PRNT_STATUS, example = ModelDocs.BlSummaryDto.EX_ECOM_PRNT_STATUS)
    private EcomPrintStatus ecomPrntstatus;
    
    @Schema(description = ModelDocs.BlSummaryDto.ORIGINALS, example = ModelDocs.BlSummaryDto.EX_ORIGINALS)
    private Integer originals;
    
    @Schema(description = ModelDocs.BlSummaryDto.COPIES, example = ModelDocs.BlSummaryDto.EX_COPIES)
    private Integer copies;
    
    @Schema(description = ModelDocs.BlSummaryDto.AGENCY_ISSUE_ID, example = ModelDocs.BlSummaryDto.EX_AGENCY_ISSUE_ID)
    private String agencyissueid;
    
    @Schema(description = "Receipt/ship date in port local time (ISO 8601 with offset)", example = "2023-11-23T15:30:00+08:00")
    private OffsetDateTime receiptshipdate;
    
    @Schema(description = ModelDocs.BlSummaryDto.FCN_VER_NO, example = ModelDocs.BlSummaryDto.EX_FCN_VER_NO)
    private String fcnverno;
    
    @Schema(description = ModelDocs.BlSummaryDto.FCN_NO, example = ModelDocs.BlSummaryDto.EX_FCN_NO)
    private String fcnno;
    
    @Schema(description = ModelDocs.BlSummaryDto.ECOM_SI_NO, example = ModelDocs.BlSummaryDto.EX_ECOM_SI_NO)
    private String ecomsino;
    
    @Schema(hidden = true)
    @JsonIgnore
    private String policyNo;
}
