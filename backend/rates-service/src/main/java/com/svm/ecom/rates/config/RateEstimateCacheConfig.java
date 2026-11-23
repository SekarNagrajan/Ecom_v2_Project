package com.svm.ecom.rates.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables scheduled refresh for {@link com.svm.ecom.rates.service.RateEstimateLookupCacheService}
 * (same 30-min pattern as Booking {@code MasterDataCacheService}).
 */
@Configuration
@EnableScheduling
public class RateEstimateCacheConfig {
}
