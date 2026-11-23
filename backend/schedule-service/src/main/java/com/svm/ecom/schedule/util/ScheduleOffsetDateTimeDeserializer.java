package com.svm.ecom.schedule.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.svm.ecom.common.util.PortLocalTimeUtil;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Lenient deserializer for OffsetDateTime when parsing external schedule API response.
 * Accepts ISO-8601 strings (with or without offset), space-separated date-time (e.g. "2026-03-03 17:00:00.0"),
 * and epoch milliseconds. Values without offset are treated as UTC.
 */
public class ScheduleOffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentToken()) {
            case VALUE_NULL:
                return null;
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
                long epoch = p.getLongValue();
                return OffsetDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
            case VALUE_STRING:
                String value = p.getText();
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                Instant inst = PortLocalTimeUtil.parseToInstant(value.trim());
                return inst == null ? null : OffsetDateTime.ofInstant(inst, ZoneOffset.UTC);
            default:
                return null;
        }
    }
}
