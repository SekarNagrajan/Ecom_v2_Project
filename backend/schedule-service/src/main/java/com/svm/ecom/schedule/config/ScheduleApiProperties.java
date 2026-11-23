package com.svm.ecom.schedule.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for schedule external API
 */
@Configuration
@ConfigurationProperties(prefix = "ecom.schedule.api")
public class ScheduleApiProperties {

    /**
     * Base URL for the external schedule API
     */
    @Value("${ecom.tracking.api.base-url}")
    private String baseUrl;

    /**
     * Connection timeout in milliseconds
     */
    private int connectionTimeout = 30000;

    /**
     * Read timeout in milliseconds
     */
    private int readTimeout = 60000;

    /**
     * Maximum number of retry attempts
     */
    private int maxRetries = 3;

    /**
     * Retry delay in milliseconds
     */
    private int retryDelay = 1000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }
}