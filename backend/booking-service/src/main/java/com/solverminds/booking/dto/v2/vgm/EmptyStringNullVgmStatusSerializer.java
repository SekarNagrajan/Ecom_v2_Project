package com.solverminds.booking.dto.v2.vgm;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Serializes absent VGM status as {@code ""} for GET responses, matching other optional string fields.
 */
public class EmptyStringNullVgmStatusSerializer extends JsonSerializer<VgmStatus> {

    @Override
    public void serialize(VgmStatus value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeString("");
        } else {
            gen.writeString(value.name());
        }
    }
}
