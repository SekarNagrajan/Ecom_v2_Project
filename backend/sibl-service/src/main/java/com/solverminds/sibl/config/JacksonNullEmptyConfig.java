package com.solverminds.sibl.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configures Jackson to serialize null as empty in API responses:
 * null String → "", null Number → 0, null LocalDateTime → "".
 * Lists use DtoUtil.emptyListIfNull() in DTO getters for [].
 */
@Configuration
public class JacksonNullEmptyConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonNullAsEmptyCustomizer() {
        return builder -> {
            SimpleModule module = new SimpleModule("NullAsEmpty");
            module.addSerializer(String.class, new JsonSerializer<String>() {
                @Override
                public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeString(value == null ? "" : value);
                }
            });
            module.addSerializer(Integer.class, new JsonSerializer<Integer>() {
                @Override
                public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeNumber(value == null ? 0 : value);
                }
            });
            module.addSerializer(Long.class, new JsonSerializer<Long>() {
                @Override
                public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeNumber(value == null ? 0L : value);
                }
            });
            module.addSerializer(Double.class, new JsonSerializer<Double>() {
                @Override
                public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeNumber(value == null ? 0.0 : value);
                }
            });
            module.addSerializer(Float.class, new JsonSerializer<Float>() {
                @Override
                public void serialize(Float value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    gen.writeNumber(value == null ? 0.0f : value);
                }
            });
            module.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    if (value == null) {
                        gen.writeString("");
                    } else {
                        gen.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                }
            });
            builder.modules(module);
        };
    }
}
