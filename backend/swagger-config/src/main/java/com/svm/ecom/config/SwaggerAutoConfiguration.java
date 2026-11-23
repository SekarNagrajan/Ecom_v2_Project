package com.svm.ecom.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for Swagger components to ensure all controllers are properly scanned
 */
@Configuration
@ComponentScan(basePackages = {
    "com.svm.ecom.config"  // Ensure all controllers in this package are scanned
})
public class SwaggerAutoConfiguration {
    // This class ensures proper component scanning for Swagger controllers
}
