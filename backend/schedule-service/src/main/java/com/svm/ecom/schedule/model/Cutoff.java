package com.svm.ecom.schedule.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * Cutoff information containing document, VGM, and container closing times.
 * All times are in origin port local time as OffsetDateTime.
 * Accepts external API keys in camelCase (docClosing) or snake_case (doc_closing).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Cutoff information containing document, VGM, and container closing times (in origin port local time with timezone offset)")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cutoff {

    @Schema(description = "Document closing date and time in origin port local time (ISO 8601 with offset)", example = "2025-12-07T20:00:00+08:00")
    @JsonAlias("doc_closing")
    private OffsetDateTime docClosing;

    @Schema(description = "VGM closing date and time in origin port local time (ISO 8601 with offset)", example = "2025-12-07T20:00:00+08:00")
    @JsonAlias("vgm_closing")
    private OffsetDateTime vgmClosing;

    @Schema(description = "Container closing date and time in origin port local time (ISO 8601 with offset)", example = "2025-12-07T20:00:00+08:00")
    @JsonAlias("cont_closing")
    private OffsetDateTime contClosing;

    // Default constructor
    public Cutoff() {}

    // Constructor with all parameters
    public Cutoff(OffsetDateTime docClosing, OffsetDateTime vgmClosing, OffsetDateTime contClosing) {
        this.docClosing = docClosing;
        this.vgmClosing = vgmClosing;
        this.contClosing = contClosing;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "Cutoff{" +
                "docClosing=" + docClosing +
                ", vgmClosing=" + vgmClosing +
                ", contClosing=" + contClosing +
                '}';
    }
}
