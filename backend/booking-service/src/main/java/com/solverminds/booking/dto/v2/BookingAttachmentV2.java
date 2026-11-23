package com.solverminds.booking.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Metadata of a booking attachment for V2 API.
 */
@Schema(name = "BookingAttachment", description = "Metadata of a booking attachment")
public class BookingAttachmentV2 {

    @NotBlank
    @Schema(description = "Attachment category", example = "DGC", allowableValues = {"DGC", "VGM"})
    private String category;

    @NotBlank
    @Schema(description = "Name of the attachment file", example = "BKON123_DGC.pdf")
    private String name;

    @NotNull
    @Schema(description = "Size of the attachment file in bytes", example = "123456")
    private Long size;

    public BookingAttachmentV2() {
    }

    public BookingAttachmentV2(String category, String name, Long size) {
        this.category = category;
        this.name = name;
        this.size = size;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}

