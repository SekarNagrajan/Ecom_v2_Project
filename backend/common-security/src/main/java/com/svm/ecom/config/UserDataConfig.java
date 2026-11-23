package com.svm.ecom.config;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Database configuration for User Authentication database.
 * This configuration sets up the connection to User MSSQL database via Spring Boot properties
 * and configures JPA with Hibernate for user entity management.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.svm.ecom.infrastructure.user",
    entityManagerFactoryRef = "userEntityManagerFactory",
    transactionManagerRef = "userTransactionManager"
)
@EnableTransactionManagement
public class UserDataConfig {

    // Note: userDataSource bean is defined in BeansConfig (register-service module)
    // to avoid bean definition conflicts. It provides fallback logic between
    // ecomDataSource and primaryDataSource.

    /**
     * Configure the JPA EntityManagerFactory.
     * Sets up Hibernate as the JPA implementation with MSSQL Server specific settings.
     *
     * @param userDataSource the User DataSource bean
     * @return LocalContainerEntityManagerFactoryBean

     */
    @Bean(name = "userEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean userEntityManagerFactory(@Qualifier("userDataSource") DataSource userDataSource) {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(userDataSource);
        factory.setPackagesToScan("com.svm.ecom.domain.user");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaProperties(userJpaProperties());
        factory.setPersistenceUnitName("userPersistenceUnit");
        return factory;
    }

    /**
     * Configure JPA/Hibernate properties for MSSQL Server.
     *
     * @return Properties object with Hibernate configuration
     */
    private Properties userJpaProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "validate"); // Use 'update' for development, 'validate' for production
        props.setProperty("hibernate.show_sql", "false"); // Set to true for development debugging
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "20");
        props.setProperty("hibernate.cache.use_second_level_cache", "false");
        props.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return props;
    }

    /**
     * Configure the transaction manager for JPA.
     *
     * @param entityManagerFactory the EntityManagerFactory bean
     * @return PlatformTransactionManager for handling database transactions
     */
    @Bean(name = "userTransactionManager")
    public PlatformTransactionManager userTransactionManager(
            @Qualifier("userEntityManagerFactory") EntityManagerFactory userEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(userEntityManagerFactory);
        return transactionManager;
    }
}