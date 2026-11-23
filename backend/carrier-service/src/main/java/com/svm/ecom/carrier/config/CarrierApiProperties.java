package com.svm.ecom.carrier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Live host and tokens for the coastal and long range carrier schedule APIs.
 */
@Configuration
public class CarrierApiProperties {

    @Value("${ecom.schedule.coastal.base-url:}")
    private String coastalBaseUrl;

    @Value("${ecom.schedule.coastal.token:}")
    private String coastalToken;

    @Value("${ecom.schedule.long-range.token:}")
    private String longRangeToken;

    private int connectionTimeout = 30000;
    private int readTimeout = 60000;
    private int maxRetries = 3;
    private int retryDelay = 1000;

    public String getCoastalBaseUrl() {
        return coastalBaseUrl;
    }

    public void setCoastalBaseUrl(String coastalBaseUrl) {
        this.coastalBaseUrl = coastalBaseUrl;
    }

    public String getCoastalToken() {
        return coastalToken;
    }

    public void setCoastalToken(String coastalToken) {
        this.coastalToken = coastalToken;
    }

    public String getLongRangeToken() {
        return longRangeToken;
    }

    public void setLongRangeToken(String longRangeToken) {
        this.longRangeToken = longRangeToken;
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
