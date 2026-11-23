package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Cut-off times for port/vessel schedule rows (doc, VGM, full container closing).
 * Populated from {@code ecom_cutoffconfig} via {@link com.svm.ecom.schedule.service.CutoffCalculator}.
 */
@Schema(description = "Cut-off times derived from port configuration and arrival time")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CutoffVo {

    @Schema(description = "Documentation closing (ISO 8601 with offset)", example = "2026-04-10T14:00:00+08:00")
    @JsonProperty("docClosing")
    private OffsetDateTime docClosing;

    @Schema(description = "VGM closing (ISO 8601 with offset)", example = "2026-04-10T14:00:00+08:00")
    @JsonProperty("vgmClosing")
    private OffsetDateTime vgmClosing;

    @Schema(description = "Full container closing (ISO 8601 with offset)", example = "2026-04-10T14:00:00+08:00")
    @JsonProperty("contClosing")
    private OffsetDateTime contClosing;

    public CutoffVo() {
    }

    public OffsetDateTime getDocClosing() {
        return docClosing;
    }

    public void setDocClosing(OffsetDateTime docClosing) {
        this.docClosing = docClosing;
    }

    public OffsetDateTime getVgmClosing() {
        return vgmClosing;
    }

    public void setVgmClosing(OffsetDateTime vgmClosing) {
        this.vgmClosing = vgmClosing;
    }

    public OffsetDateTime getContClosing() {
        return contClosing;
    }

    public void setContClosing(OffsetDateTime contClosing) {
        this.contClosing = contClosing;
    }
}
