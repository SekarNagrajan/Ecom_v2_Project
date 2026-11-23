package com.svm.ecom.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Jackson configuration for the application.
 * Provides flexible LocalDateTime deserialization to handle various date formats
 * from external APIs while outputting consistent ISO-8601 format with 'T' separator.
 * 
 * Note: @JsonFormat annotations on fields will override the default serialization format.
 * - Use @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC") for UTC timestamps
 * - Use @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") for local date-times
 */
@Configuration
public class JacksonConfig {

    // Default output format without milliseconds: yyyy-MM-dd'T'HH:mm:ss
    private static final DateTimeFormatter DEFAULT_OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Custom deserializer for LocalDateTime that handles multiple input formats.
     * Accepts dates with space or 'T' separator, with or without milliseconds/nanoseconds.
     */
    public static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        
        private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            // With space separator (from external API)
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            // With 'T' separator (ISO-8601 standard)
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
            // ISO standard
            DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
        
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateString = p.getValueAsString();
            
            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }
            
            dateString = dateString.trim();
            
            // Strip timezone suffix so we keep the local date-time as-is (no UTC conversion)
            // Handles: "Z", "+05:30", "-04:00", "+00:00" etc.
            dateString = stripTimezoneOffset(dateString);
            
            // Try each formatter until one works
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                    // Try next format
                }
            }
            
            // If none worked, throw an informative error
            throw new IOException("Unable to parse date: '" + dateString + 
                "'. Expected format: yyyy-MM-dd HH:mm:ss or yyyy-MM-dd'T'HH:mm:ss");
        }

        private static String stripTimezoneOffset(String dateString) {
            if (dateString.endsWith("Z")) {
                return dateString.substring(0, dateString.length() - 1);
            }
            // Match trailing +HH:MM or -HH:MM (e.g. +05:30, -04:00)
            int len = dateString.length();
            if (len > 6) {
                char sign = dateString.charAt(len - 6);
                if ((sign == '+' || sign == '-') && dateString.charAt(len - 3) == ':') {
                    return dateString.substring(0, len - 6);
                }
            }
            return dateString;
        }
    }

    /**
     * Configure the primary ObjectMapper with flexible date handling.
     * This ensures incoming dates with space separators are parsed correctly,
     * while outgoing dates are formatted without milliseconds per ISO-8601.
     * 
     * Fields can override the default format using @JsonFormat annotation.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Create JavaTimeModule with custom default serializer (without milliseconds)
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DEFAULT_OUTPUT_FORMATTER));
        mapper.registerModule(javaTimeModule);
        
        // Create custom module with our flexible deserializer
        SimpleModule customModule = new SimpleModule();
        customModule.addDeserializer(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
        mapper.registerModule(customModule);
        
        // Disable writing dates as timestamps (use ISO-8601 strings instead)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Preserve the original timezone offset on OffsetDateTime fields (e.g. ETD/ETA)
        // instead of normalizing to UTC during deserialization
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        
        return mapper;
    }
}
