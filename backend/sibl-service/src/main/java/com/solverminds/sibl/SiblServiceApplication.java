package com.solverminds.sibl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
    scanBasePackages = {
        "com.solverminds.sibl",
        "com.solverminds.booking.common", // booking-common repos for view-by-bookingNo (Documentation DB)
        "com.svm.ecom.security", // pick up JwtUtil + any fallback beans from common-security
        "com.svm.ecom.config",   // pick up Swagger configuration and database configs
        "com.svm.ecom.util",     // pick up utility classes
        "com.svm.ecom.common"    // pick up common components
    },
    exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
    }
)
public class SiblServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SiblServiceApplication.class, args);
    }
}
