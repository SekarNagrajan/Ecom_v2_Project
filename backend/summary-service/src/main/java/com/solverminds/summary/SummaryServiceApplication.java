package com.solverminds.summary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.solverminds.summary",
    "com.svm.ecom.security", // pick up JwtUtil + any fallback beans from common-security
    "com.svm.ecom.config" // pick up Swagger configuration
})
public class SummaryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SummaryServiceApplication.class, args);
    }
}
