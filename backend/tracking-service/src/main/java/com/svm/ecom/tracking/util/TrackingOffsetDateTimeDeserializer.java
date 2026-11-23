package com.svm.ecom.tracking.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.svm.ecom.common.util.PortLocalTimeUtil;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.Instant;

/**
 * Deserializes date strings from the tracking API (various formats) to OffsetDateTime at UTC.
 * Uses DateUtils.toIsoFormat and PortLocalTimeUtil.parseToInstant for compatibility.
 */
public class TrackingOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        String iso = DateUtils.toIsoFormat(value.trim());
        Instant inst = PortLocalTimeUtil.parseToInstant(iso);
        return inst == null ? null : OffsetDateTime.ofInstant(inst, ZoneOffset.UTC);
    }
}
