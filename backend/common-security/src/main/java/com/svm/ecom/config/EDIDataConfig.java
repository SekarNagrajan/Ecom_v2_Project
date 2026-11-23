package com.svm.ecom.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
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
 * Database configuration for EDI (Electronic Data Interchange) database.
 * This configuration sets up the connection to EDI MSSQL database via Spring Boot properties
 * and configures JPA with Hibernate for EDI entity management.
 * 
 * EDI tables (editempbooking, editempbooking_cargo, etc.) are in a separate database
 * from the ECOM tables. This configuration handles all EDI-related data access.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = {"com.solverminds.booking.repo", "com.solverminds.sibl.repo"},  // Scan booking and sibl repo packages
    includeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.solverminds\\.(booking|sibl)\\.repo\\.Edi.*"  // Include ALL EDI repos (EdiTemp*, EdiDoc*, etc.) from both packages
    ),
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.solverminds\\.(booking|sibl)\\.repo\\.Ecom.*"  // Exclude ECOM repos
    ),
    entityManagerFactoryRef = "ediEntityManagerFactory",
    transactionManagerRef = "ediTransactionManager"
)
public class EDIDataConfig {

    /**
     * Configure EDI DataSource using Spring Boot properties.
     * Configuration is loaded from application.properties with prefix 'edi.datasource'
     *
     * @return DataSource bean configured via properties
     */
    @Bean(name = "ediDataSource")
    @ConfigurationProperties(prefix = "edi.datasource")
    public DataSource ediDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Configure the JPA EntityManagerFactory for EDI.
     * Sets up Hibernate as the JPA implementation with MSSQL Server specific settings.
     *
     * @return LocalContainerEntityManagerFactoryBean for EDI
     */
    @Bean(name = "ediEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean ediEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(ediDataSource());
        factory.setPackagesToScan("com.solverminds.edi.entity");  // EDI entities from edi-common module
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaProperties(ediJpaProperties());
        factory.setPersistenceUnitName("ediPersistenceUnit");
        return factory;
    }

    /**
     * Configure JPA/Hibernate properties for EDI MSSQL Server.
     *
     * @return Properties object with Hibernate configuration for EDI
     */
    private Properties ediJpaProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "none");
        props.setProperty("hibernate.show_sql", "true");
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "20");
        props.setProperty("hibernate.cache.use_second_level_cache", "false");
        props.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return props;
    }

    /**
     * Configure the transaction manager for EDI JPA.
     *
     * @param ediEntityManagerFactory the EntityManagerFactory bean for EDI
     * @return PlatformTransactionManager for handling EDI database transactions
     */
    @Bean(name = "ediTransactionManager")
    public PlatformTransactionManager ediTransactionManager(
            @Qualifier("ediEntityManagerFactory") EntityManagerFactory ediEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(ediEntityManagerFactory);
        return transactionManager;
    }

    /**
     * Configure JdbcTemplate for EDI database.
     * This enables raw SQL queries and stored procedure calls on the EDI database.
     *
     * @return JdbcTemplate configured with EDI DataSource
     */
    @Bean(name = "ediJdbcTemplate")
    public JdbcTemplate ediJdbcTemplate() {
        return new JdbcTemplate(ediDataSource());
    }
}

