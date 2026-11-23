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
 * Database configuration for Documentation database.
 * This configuration sets up the connection to Documentation MSSQL database via Spring Boot properties
 * and configures JPA with Hibernate for Documentation entity management.
 * 
 * Scans packages:
 * - com.svm.ecom.infrastructure.documentation (legacy repositories)
 * - com.solverminds.booking.repository (booking-common repositories)
 */
@Configuration
@EnableJpaRepositories(
    basePackages = {
        "com.svm.ecom.infrastructure.documentation",
        "com.solverminds.booking.common.repository",
        "com.solverminds.bl.common.repository"
    },
    entityManagerFactoryRef = "documentationEntityManagerFactory",
    transactionManagerRef = "documentationTransactionManager"
)
public class DocumentationDataConfig {

    /**
     * Configure Documentation DataSource using Spring Boot properties.
     * Configuration is loaded from application.properties with prefix 'documentation.datasource'
     *
     * @return DataSource bean configured via properties
     */
    @Bean(name = "documentationDataSource")
    @ConfigurationProperties(prefix = "documentation.datasource")
    public DataSource documentationDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Configure the JPA EntityManagerFactory for Documentation.
     * Sets up Hibernate as the JPA implementation with MSSQL Server specific settings.
     *
     * @return LocalContainerEntityManagerFactoryBean for Documentation

     */
    @Bean(name = "documentationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean documentationEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(documentationDataSource());
        factory.setPackagesToScan(
            "com.svm.ecom.domain.documentation",
            "com.solverminds.booking.common.entity",  // booking-common entities
            "com.solverminds.bl.common.entity"       // bl-common entities
        );
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaProperties(documentationJpaProperties());
        factory.setPersistenceUnitName("documentationPersistenceUnit");
        return factory;
    }

    /**
     * Configure JPA/Hibernate properties for Documentation MSSQL Server.
     *
     * @return Properties object with Hibernate configuration for Documentation
     */
    private Properties documentationJpaProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "none"); // Disabled for view-only entities - no DDL validation needed
        props.setProperty("hibernate.show_sql", "false"); // Set to true for development debugging
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "20");
        props.setProperty("hibernate.cache.use_second_level_cache", "false");
        props.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return props;
    }

    /**
     * Configure the transaction manager for Documentation JPA.
     *
     * @param documentationEntityManagerFactory the EntityManagerFactory bean for Documentation
     * @return PlatformTransactionManager for handling Documentation database transactions
     */
    @Bean(name = "documentationTransactionManager")
    public PlatformTransactionManager documentationTransactionManager(
            @Qualifier("documentationEntityManagerFactory") EntityManagerFactory documentationEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(documentationEntityManagerFactory);
        return transactionManager;
    }

    /**
     * Configure JdbcTemplate for Documentation database.
     * This enables raw SQL queries and stored procedure calls on the Documentation database.
     *
     * @return JdbcTemplate configured with Documentation DataSource
     */
    @Bean(name = "documentationJdbcTemplate")
    public JdbcTemplate documentationJdbcTemplate() {
        return new JdbcTemplate(documentationDataSource());
    }
}