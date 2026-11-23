package com.svm.ecom.rates.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Isolated Commercial database configuration for Booking CommonSurcharge lookups
 * and SRR OFR ({@code srrdetail}) charge reads. Does not replace ECOM / Report datasources.
 */
@Configuration
public class CommercialDataConfig {

    private static final Logger logger = LoggerFactory.getLogger(CommercialDataConfig.class);

    @Bean(name = "commercialDataSource")
    @ConfigurationProperties(prefix = "commercial.datasource")
    public DataSource commercialDataSource() {
        logger.info("Commercial DataSource bean registered for rates-service");
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "commercialJdbcTemplate")
    public JdbcTemplate commercialJdbcTemplate(@Qualifier("commercialDataSource") DataSource commercialDataSource) {
        return new JdbcTemplate(commercialDataSource);
    }
}
