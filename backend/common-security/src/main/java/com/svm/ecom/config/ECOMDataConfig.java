package com.svm.ecom.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
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
 * Database configuration for ECOM (E-Commerce) database.
 * This configuration sets up the connection to ECOM MSSQL database via Spring Boot properties
 * and configures JPA with Hibernate for ECOM entity management.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = {
        "com.svm.ecom.infrastructure.ecom", 
        "com.solverminds.ecom.register.repo",
        "com.solverminds.booking.repo",  // ECOM booking repositories (EcomBooking*)
        "com.solverminds.sibl.repo"
    },
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.REGEX,
        pattern = "com\\.solverminds\\.(booking|sibl)\\.repo\\.Edi.*"  // Exclude ALL EDI repos from both booking and sibl packages - they use EDI database
    ),
    entityManagerFactoryRef = "ecomEntityManagerFactory",
    transactionManagerRef = "ecomTransactionManager"
)
public class ECOMDataConfig {

    /**
     * Configure ECOM DataSource using Spring Boot properties.
     * Configuration is loaded from application.properties with prefix 'ecom.datasource'
     *
     * @return DataSource bean configured via properties
     */
    @Bean(name = "ecomDataSource")
    @ConfigurationProperties(prefix = "ecom.datasource")
    public DataSource ecomDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Configure the JPA EntityManagerFactory for ECOM.
     * Sets up Hibernate as the JPA implementation with MSSQL Server specific settings.
     *
     * @return LocalContainerEntityManagerFactoryBean for ECOM

     */
    @Bean(name = "ecomEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean ecomEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(ecomDataSource());
        factory.setPackagesToScan(
            "com.svm.ecom.domain.ecom", 
            "com.solverminds.ecom.register.entity", 
            "com.solverminds.booking.entity", 
            "com.solverminds.sibl.entity"
            // EDI entities are in separate database - see EDIDataConfig
        );
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaProperties(ecomJpaProperties());
        factory.setPersistenceUnitName("ecomPersistenceUnit");
        return factory;
    }

    /**
     * Configure JPA/Hibernate properties for ECOM MSSQL Server.
     *
     * @return Properties object with Hibernate configuration for ECOM
     */
    private Properties ecomJpaProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "none"); // Temporarily disabled validation to test connection
        props.setProperty("hibernate.show_sql", "true"); // Enabled for debugging table connection
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "20");
        props.setProperty("hibernate.cache.use_second_level_cache", "false");
        props.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return props;
    }

    /**
     * Configure the transaction manager for ECOM JPA.
     *
     * @param ecomEntityManagerFactory the EntityManagerFactory bean for ECOM
     * @return PlatformTransactionManager for handling ECOM database transactions
     */
    @Bean(name = "ecomTransactionManager")
    public PlatformTransactionManager ecomTransactionManager(
            @Qualifier("ecomEntityManagerFactory") EntityManagerFactory ecomEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(ecomEntityManagerFactory);
        return transactionManager;
    }

    /**
     * Configure JdbcTemplate for ECOM database.
     * This enables raw SQL queries and stored procedure calls on the ECOM database.
     * This is the primary JdbcTemplate for most business operations.
     *
     * @return JdbcTemplate configured with ECOM DataSource
     */
    @Bean(name = "ecomJdbcTemplate")
    @Primary
    public JdbcTemplate ecomJdbcTemplate() {
        return new JdbcTemplate(ecomDataSource());
    }
}