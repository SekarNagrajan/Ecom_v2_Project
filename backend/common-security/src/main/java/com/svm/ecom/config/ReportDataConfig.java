package com.svm.ecom.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Database configuration for Report database (Master Data).
 * This configuration sets up the connection to Report MSSQL database via Spring Boot properties
 * and configures JPA with Hibernate for master data entity management.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.svm.ecom.master.repository",
    entityManagerFactoryRef = "reportEntityManagerFactory",
    transactionManagerRef = "reportTransactionManager"
)
public class ReportDataConfig {

    /**
     * Configure Report DataSource using Spring Boot properties.
     * Configuration is loaded from application.properties with prefix 'report.datasource'
     *
     * @return DataSource bean configured via properties
     */
    @Bean(name = "reportDataSource")
    @ConfigurationProperties(prefix = "report.datasource")
    public DataSource reportDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Configure the JPA EntityManagerFactory for Report.
     * Sets up Hibernate as the JPA implementation with MSSQL Server specific settings.
     *
     * @return LocalContainerEntityManagerFactoryBean for Report
     */
    @Bean(name = "reportEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean reportEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(reportDataSource());
        factory.setPackagesToScan("com.svm.ecom.master.model");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaProperties(reportJpaProperties());
        factory.setPersistenceUnitName("reportPersistenceUnit");
        return factory;
    }

    /**
     * Configure JPA/Hibernate properties for Report MSSQL Server.
     *
     * @return Properties object with Hibernate configuration for Report
     */
    private Properties reportJpaProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "none"); // No schema validation - using native queries instead
        props.setProperty("hibernate.show_sql", "false"); // Set to true for development debugging
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "20");
        props.setProperty("hibernate.cache.use_second_level_cache", "false");
        props.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return props;
    }

    /**
     * Configure the transaction manager for Report JPA.
     *
     * @param reportEntityManagerFactory the EntityManagerFactory bean for Report
     * @return PlatformTransactionManager for handling Report database transactions
     */
    @Bean(name = "reportTransactionManager")
    public PlatformTransactionManager reportTransactionManager(
            @Qualifier("reportEntityManagerFactory") EntityManagerFactory reportEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(reportEntityManagerFactory);
        return transactionManager;
    }

    /**
     * Configure JdbcTemplate for Report database.
     * This enables raw SQL queries and stored procedure calls on the Report database.
     *
     * @return JdbcTemplate configured with Report DataSource
     */
    @Bean(name = "reportJdbcTemplate")
    public JdbcTemplate reportJdbcTemplate() {
        return new JdbcTemplate(reportDataSource());
    }

    /**
     * Alias for the LRP / operational report database — same {@link DataSource} as
     * {@link #reportJdbcTemplate()}. Used by booking cancellation (booking, plotdomaster, bl, etc.).
     */
    @Bean(name = "lrpJdbcTemplate")
    public JdbcTemplate lrpJdbcTemplate(@Qualifier("reportJdbcTemplate") JdbcTemplate reportJdbcTemplate) {
        return reportJdbcTemplate;
    }
}
