package com.svm.ecom.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Swagger UI customization configuration
 * Automatically sets the server URL based on the active Spring profile
 */
@Configuration
public class SwaggerCustomization implements WebMvcConfigurer {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    /**
     * Dynamically add server based on active Spring profile
     * This prevents users from accidentally hitting wrong environments
     * Order.LOWEST_PRECEDENCE ensures this runs LAST, overriding any auto-detected servers
     */
    @Bean
    @Order(org.springframework.core.Ordered.LOWEST_PRECEDENCE)
    public OpenApiCustomiser addServersCustomizer() {
        return openApi -> {
            String serverUrl;
            String description;
            
            // Determine server URL based on active profile
            if (activeProfile.contains("prod")) {
                serverUrl = "https://erp.tailwind-shipping.com" + contextPath;
                description = "Production Server";
            } else if (activeProfile.contains("uat")) {
                serverUrl = "https://erp-uat.tailwind-shipping.com" + contextPath;
                description = "UAT Server";
            } else {
                // Default to local development (includes 'company', 'default', or any other profile)
                serverUrl = "http://localhost:8080" + contextPath;
                description = "Local Development Server";
            }
            
            // FORCE clear any auto-detected servers first
            if (openApi.getServers() != null) {
                openApi.getServers().clear();
            }
            
            // Set only the appropriate server for the current environment
            openApi.setServers(java.util.Collections.singletonList(
                new io.swagger.v3.oas.models.servers.Server()
                    .url(serverUrl)
                    .description(description)
            ));
            
            System.out.println("========================================");
            System.out.println("Swagger Server Configuration Applied:");
            System.out.println("URL: " + serverUrl);
            System.out.println("Description: " + description);
            System.out.println("Active Profile: " + activeProfile);
            System.out.println("========================================");
        };
    }

    /**
     * Add custom CSS and JS resource handlers to hide any remaining server elements
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui-hide-servers.css")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/swagger-ui-hide-servers.js")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/swagger-ui-no-servers.js")
                .addResourceLocations("classpath:/static/");
    }
}
