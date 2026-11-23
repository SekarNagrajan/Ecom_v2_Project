package com.svm.ecom.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to verify Swagger configuration and provide helpful debug information.
 */
@RestController
@RequestMapping("/swagger-test")
@Hidden
public class SwaggerTestController {

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSwaggerStatus(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        // Basic application info
        response.put("status", "OK");
        response.put("contextPath", request.getContextPath());
        response.put("serverName", request.getServerName());
        response.put("serverPort", request.getServerPort());
        response.put("requestURL", request.getRequestURL().toString());
        
        // Swagger UI access URLs
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        response.put("swaggerUI", baseUrl + "/swagger-ui/index.html");
        response.put("apiDocs", baseUrl + "/v3/api-docs");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/links")
    public String getSwaggerLinks(HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        
        return "<html><body>" +
                "<h2>ECOM API - Swagger Access</h2>" +
                "<ul>" +
                "<li><a href='" + baseUrl + "/swagger-ui/index.html' target='_blank'>Swagger UI</a></li>" +
                "<li><a href='" + baseUrl + "/v3/api-docs' target='_blank'>OpenAPI JSON</a></li>" +
                "<li><a href='" + baseUrl + "/swagger-test/status' target='_blank'>Status Check</a></li>" +
                "</ul>" +
                "<p><strong>Context Path:</strong> " + request.getContextPath() + "</p>" +
                "<p><strong>Base URL:</strong> " + baseUrl + "</p>" +
                "</body></html>";
    }
}