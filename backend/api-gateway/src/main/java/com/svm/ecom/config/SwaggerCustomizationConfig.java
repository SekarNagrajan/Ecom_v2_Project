package com.svm.ecom.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerCustomizationConfig {

    @Bean
    public OpenApiCustomiser openApiCustomiser() {
        return openApi -> {
            // Remove contact information
            Info info = openApi.getInfo();
            if (info != null) {
                info.setContact(null);
                
                // Clean up description to remove contact references
                String description = info.getDescription();
                if (description != null) {
                    description = description.replaceAll("ECOM API Support Team.*", "");
                    description = description.replaceAll("Send email to.*", "");
                    info.setDescription(description.trim());
                }
            }
            
            // Remove servers to hide the servers dropdown
            openApi.setServers(null);
        };
    }
}
