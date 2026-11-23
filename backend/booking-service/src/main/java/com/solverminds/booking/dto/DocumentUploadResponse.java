package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for individual uploaded document
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Document upload response")
public class DocumentUploadResponse {

    @Schema(description = "Name of the uploaded file", example = "invoice.pdf")
    private String fileName;
    @Schema(description = "Size of the uploaded file in bytes", example = "123456")
    private Long sizeBytes;
    @Schema(description = "File upload success message", example = "File uploaded successfully")
    private String error; // Only populated if this file failed validation

    public DocumentUploadResponse() {
    }

    public DocumentUploadResponse(String fileName, Long sizeBytes) {
        this.fileName = fileName;
        this.sizeBytes = sizeBytes;
    }

    public DocumentUploadResponse(String fileName, String error) {
        this.fileName = fileName;
        this.error = error;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

