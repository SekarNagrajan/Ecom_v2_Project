package com.solverminds.booking.dto.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Metadata of a single document attached to a booking.
 * Field names and types match the client OpenAPI specification exactly.
 */
@Schema(description = "Metadata of a single document.")
public class DocumentMetadata {

    @Schema(description = "Name of the document file",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "customs_manifest.pdf")
    private String fileName;

    @Schema(description = "Size of the file in bytes",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "102400")
    private Long fileSize;

    @Schema(description = "Last modified timestamp in UTC",
            requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string",
            format = "date-time",
            example = "2026-01-05T09:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private OffsetDateTime lastModified;

    public DocumentMetadata() {
    }

    public DocumentMetadata(String fileName, Long fileSize, OffsetDateTime lastModified) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.lastModified = lastModified;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(OffsetDateTime lastModified) {
        this.lastModified = lastModified;
    }
}
