package com.solverminds.booking.dto.v2.vgm;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Serializes absent numeric VGM weight as {@code ""} for GET responses, matching other optional string fields.
 */
public class EmptyStringNullDoubleSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeString("");
        } else {
            gen.writeNumber(value);
        }
    }
}
