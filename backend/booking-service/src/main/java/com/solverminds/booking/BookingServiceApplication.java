package com.solverminds.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "com.solverminds.booking",
    "com.svm.ecom.security", // pick up JwtUtil + any fallback beans from common-security
    "com.svm.ecom.config", // pick up Swagger configuration and data sources
    "com.svm.ecom.common.master" // shared Documentation DG master lookups (IMDG / packaging)
})
@EntityScan(basePackages = {
    "com.solverminds.booking.entity",  // Booking service entities
    "com.solverminds.edi.entity"       // EDI common entities
})
@EnableScheduling // Enable scheduled tasks for cache refresh
// Repository and entity management is handled by:
// - ECOMDataConfig: ECOM database (booking.entity -> EcomBooking*, booking.repo -> EcomBooking*)
// - EDIDataConfig: EDI database (edi.entity -> *, booking.repo -> Edi*)
// Both configs in common-security module configure proper EntityManagerFactory and TransactionManager
public class BookingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }
}
