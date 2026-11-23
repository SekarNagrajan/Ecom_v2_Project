package com.solverminds.sibl.dto;

import com.solverminds.sibl.util.DtoUtil;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.*;

@Schema(description = "File metadata attached to SI")
public class FileAttachmentDTO {
    @NotBlank private String fileCategory;  // e.g., DGC/VGM/INV/PL
    @NotBlank private String fileName;

    public FileAttachmentDTO() {}

    public String getFileCategory() { return DtoUtil.emptyIfNull(fileCategory); }
    public void setFileCategory(String fileCategory) { this.fileCategory = fileCategory; }
    public String getFileName() { return DtoUtil.emptyIfNull(fileName); }
    public void setFileName(String fileName) { this.fileName = fileName; }
}
