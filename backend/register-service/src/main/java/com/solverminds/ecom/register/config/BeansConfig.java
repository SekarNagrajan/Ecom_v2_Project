package com.solverminds.ecom.register.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class BeansConfig {

    @Autowired
    @Qualifier("ecomDataSource")
    private DataSource ecomDataSource;

    @Bean
    @Primary
    public DataSource userDataSource() {
        return ecomDataSource;
    }

    @Bean
    public EcomAuthProperties ecomAuthProperties(Environment env) {
        EcomAuthProperties p = new EcomAuthProperties();
        p.setMaxWrongAttempts(Integer.parseInt(env.getProperty("ecom.auth.maxWrongAttempts", "5")));
        p.setLockMinutes(Integer.parseInt(env.getProperty("ecom.auth.lockMinutes", "30")));
        p.setAccessTtlSeconds(Integer.parseInt(env.getProperty("ecom.auth.accessTtlSeconds", "1800")));
        p.setRefreshTtlSeconds(Integer.parseInt(env.getProperty("ecom.auth.refreshTtlSeconds", "86400")));
        p.setDefaultStatus(env.getProperty("ecom.register.default-status", "A"));
        p.setDefaultTimezone(env.getProperty("ecom.register.default-timezone", "Asia/Kolkata"));
        return p;
    }

    public static class EcomAuthProperties {
        private int maxWrongAttempts;
        private int lockMinutes;
        private int accessTtlSeconds;
        private int refreshTtlSeconds;
        private String defaultStatus;
        private String defaultTimezone;

        public int getMaxWrongAttempts() {
            return maxWrongAttempts;
        }

        public void setMaxWrongAttempts(int maxWrongAttempts) {
            this.maxWrongAttempts = maxWrongAttempts;
        }

        public int getLockMinutes() {
            return lockMinutes;
        }

        public void setLockMinutes(int lockMinutes) {
            this.lockMinutes = lockMinutes;
        }

        public int getAccessTtlSeconds() {
            return accessTtlSeconds;
        }

        public void setAccessTtlSeconds(int accessTtlSeconds) {
            this.accessTtlSeconds = accessTtlSeconds;
        }

        public int getRefreshTtlSeconds() {
            return refreshTtlSeconds;
        }

        public void setRefreshTtlSeconds(int refreshTtlSeconds) {
            this.refreshTtlSeconds = refreshTtlSeconds;
        }

        public String getDefaultStatus() {
            return defaultStatus;
        }

        public void setDefaultStatus(String defaultStatus) {
            this.defaultStatus = defaultStatus;
        }

        public String getDefaultTimezone() {
            return defaultTimezone;
        }

        public void setDefaultTimezone(String defaultTimezone) {
            this.defaultTimezone = defaultTimezone;
        }
    }
}
