package com.solverminds.booking.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Custom JSON serializer to format Double values with 2 decimal places.
 * Example: 2500 -> 2500.00, 100.5 -> 100.50
 */
public class TwoDecimalSerializer extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            BigDecimal bd = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
            gen.writeNumber(bd);
        }
    }
}
