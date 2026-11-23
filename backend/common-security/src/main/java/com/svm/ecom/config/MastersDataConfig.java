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
 * Database configuration for Masters database (Customer Master etc.).
 * Uses JPA with Hibernate for read-only native queries against Masters.
 * Configuration is loaded from application.properties with prefix 'masters.datasource'.
 */
@Configuration
@EnableJpaRepositories(
    basePackages = "com.svm.ecom.master.repository.masters",
    entityManagerFactoryRef = "mastersEntityManagerFactory",
    transactionManagerRef = "mastersTransactionManager"
)
public class MastersDataConfig {

    @Bean(name = "mastersDataSource")
    @ConfigurationProperties(prefix = "masters.datasource")
    public DataSource mastersDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mastersEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean mastersEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(mastersDataSource());
        factory.setPackagesToScan("com.svm.ecom.master.model.masters");
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setJpaProperties(mastersJpaProperties());
        factory.setPersistenceUnitName("mastersPersistenceUnit");
        return factory;
    }

    private Properties mastersJpaProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "none");
        props.setProperty("hibernate.show_sql", "false");
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.jdbc.batch_size", "20");
        props.setProperty("hibernate.cache.use_second_level_cache", "false");
        props.setProperty("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
        return props;
    }

    @Bean(name = "mastersTransactionManager")
    public PlatformTransactionManager mastersTransactionManager(
            @Qualifier("mastersEntityManagerFactory") EntityManagerFactory mastersEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(mastersEntityManagerFactory);
        return transactionManager;
    }

    @Bean(name = "mastersJdbcTemplate")
    public JdbcTemplate mastersJdbcTemplate() {
        return new JdbcTemplate(mastersDataSource());
    }
}
