package com.svm.ecom.rates.config;

import lrp.serverutils.refs.ServerObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 * Wires Spring datasources into the Booking {@link ServerObject} stub used by
 * {@code CommonSurchargeBean}.
 */
@Configuration
public class SurchargeServerObjectConfig {

    private static final Logger logger = LoggerFactory.getLogger(SurchargeServerObjectConfig.class);

    private final DataSource commercialDataSource;
    private final DataSource ecomDataSource;

    public SurchargeServerObjectConfig(
            @Qualifier("commercialDataSource") DataSource commercialDataSource,
            @Qualifier("ecomDataSource") DataSource ecomDataSource) {
        this.commercialDataSource = commercialDataSource;
        this.ecomDataSource = ecomDataSource;
    }

    @PostConstruct
    public void register() {
        // CommonSurchargeBean uses no-arg ServerObject().getConnection() — tariff data lives in Commercial.
        ServerObject.setDefaultDataSource(commercialDataSource);
        ServerObject.registerDataSource("Commercial", commercialDataSource);
        ServerObject.registerDataSource("ECOMDB", ecomDataSource);
        logger.info("ServerObject datasources registered (default=commercialDataSource)");
    }
}
