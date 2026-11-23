package com.solverminds.sibl.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.solverminds.sibl.util.DtoUtil;
import com.svm.ecom.constants.DateFormatConstants;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for listing documents in a booking folder
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Document list response")
public class DocumentListResponse {
    
    @Schema(description = "Document name in the booking folder", example = "invoice.pdf")
    private String fileName;
    @Schema(description = "Document size in bytes", example = "123456")
    private Long sizeBytes;
    @Schema(type = "string", format = "date-time", description = "Last modified timestamp (UTC, ISO-8601)", example = "2025-09-10T00:00:00Z")
    @JsonFormat(pattern = DateFormatConstants.UTC_DATE_TIME, timezone = "UTC")
    private LocalDateTime lastModified;
    @Schema(description = "Document checksum (included only if includeChecksum=true)", example = "sha256:1234567890")
    private String checksum; // Optional, only when includeChecksum=true

    public DocumentListResponse() {
    }

    public DocumentListResponse(String fileName, Long sizeBytes, LocalDateTime lastModified) {
        this.fileName = fileName;
        this.sizeBytes = sizeBytes;
        this.lastModified = lastModified;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getChecksum() {
        return DtoUtil.emptyIfNull(checksum);
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
