package com.solverminds.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

/**
 * File attachment metadata for booking documents.
 */
@Schema(description = "File metadata attached to the booking")
public class FileAttachmentDTO {

    @NotBlank
    @Schema(description = "File category (e.g., 'DGC', 'VGM')", example = "DGC")
    private String fileCategory;

    @NotBlank
    @Schema(description = "File name", example = "BKON1442_DGC.jpg")
    private String fileName;

    @Schema(description = "File size in bytes (from document list)")
    private Long fileSize;

    public FileAttachmentDTO() {
    }

    public FileAttachmentDTO(String fileCategory, String fileName) {
        this.fileCategory = fileCategory;
        this.fileName = fileName;
    }

    public FileAttachmentDTO(String fileCategory, String fileName, Long fileSize) {
        this.fileCategory = fileCategory;
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
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
}
