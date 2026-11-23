package com.solverminds.booking.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Bounded Hikari pool dedicated to {@code GET /api/v1/bookings}.
 * Clones JDBC settings from {@code reportDataSource} so this API cannot exhaust
 * the pool used by {@code GET /api/v1/booking/summary}.
 */
@Configuration
@ConditionalOnBean(name = "reportDataSource")
public class SystemBookingListDataSourceConfig {

    @Bean(name = "systemBookingListDataSource", destroyMethod = "close")
    public HikariDataSource systemBookingListDataSource(
            @Qualifier("reportDataSource") DataSource reportDataSource,
            @Value("${booking.system-list.datasource.maximum-pool-size:2}") int maximumPoolSize,
            @Value("${booking.system-list.datasource.minimum-idle:0}") int minimumIdle,
            @Value("${booking.system-list.datasource.connection-timeout-ms:10000}") long connectionTimeoutMs)
            throws SQLException {
        HikariDataSource report = reportDataSource.unwrap(HikariDataSource.class);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(report.getJdbcUrl());
        config.setUsername(report.getUsername());
        config.setPassword(report.getPassword());
        if (report.getDriverClassName() != null) {
            config.setDriverClassName(report.getDriverClassName());
        }
        if (report.getDataSourceProperties() != null) {
            config.setDataSourceProperties(report.getDataSourceProperties());
        }
        config.setPoolName("system-booking-list");
        config.setMaximumPoolSize(Math.max(1, maximumPoolSize));
        config.setMinimumIdle(Math.max(0, minimumIdle));
        config.setConnectionTimeout(connectionTimeoutMs);
        return new HikariDataSource(config);
    }

    @Bean(name = "systemBookingListJdbcTemplate")
    public JdbcTemplate systemBookingListJdbcTemplate(
            @Qualifier("systemBookingListDataSource") DataSource dataSource,
            @Value("${booking.system-list.query-timeout-seconds:30}") int queryTimeoutSeconds) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setQueryTimeout(queryTimeoutSeconds);
        return jdbcTemplate;
    }
}
