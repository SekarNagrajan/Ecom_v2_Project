package com.svm.ecom.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGroupsConfig {

  // ---- PUBLIC GROUPS ----
  @Bean
  GroupedOpenApi scheduleApi() {
    return GroupedOpenApi.builder()
        .group("schedule")
        .pathsToMatch("/api/v1/schedule/**")
        .pathsToExclude(
            "/api/v1/schedule/getscheduledetail",
            "/api/v1/schedule/getLongRangeScheduleInfo")
        .build();
  }

  @Bean
  GroupedOpenApi coastalLongRangeScheduleApi() {
    return GroupedOpenApi.builder()
        .group("schedule-coastal")
        .displayName("ECOM API - Coastal and Long Range Schedule")
        .pathsToMatch(
            "/api/v1/schedule/getscheduledetail",
            "/api/v1/schedule/getLongRangeScheduleInfo")
        .build();
  }

  @Bean
  GroupedOpenApi trackingApi() {
    return GroupedOpenApi.builder()
        .group("tracking")
        .pathsToMatch("/trackingendpoint/v1/**")
        .build();
  }

  @Bean
  GroupedOpenApi masterApi() {
    return GroupedOpenApi.builder()
        .group("master")
        .pathsToMatch("/api/v1/master/**")
        .build();
  }

  @Bean
  GroupedOpenApi masterV2Api() {
    return GroupedOpenApi.builder()
        .group("master-v2")
        .displayName("ECOM API - Master (V2)")
        .pathsToMatch(
            "/api/v2/master/agencies",
            "/api/v2/master/commodities",
            "/api/v2/master/dg-packagings",
            "/api/v2/master/imdg-entries")
        .addOpenApiCustomiser(new MasterV2OpenApiCustomiser())
        .build();
  }

  @Bean
  GroupedOpenApi customerV2Api() {
    return GroupedOpenApi.builder()
        .group("customer-v2")
        .displayName("ECOM API - Customer (V2)")
        .pathsToMatch("/api/v2/master/customers")
        .addOpenApiCustomiser(new CustomerV2OpenApiCustomiser())
        .build();
  }

  @Bean
  GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
        .group("authenticate")
        .pathsToMatch("/api/v1/auth/**")
        .build();
  }

  @Bean
  GroupedOpenApi bookingApi() {
    return GroupedOpenApi.builder()
        .group("booking")
        // "/api/v1/bookings" (exact, no trailing segment) is the SYSUSER-only
        // system-integration list endpoint on the same controller; included here so it
        // shows up in docs without affecting any other group.
        .pathsToMatch("/api/v1/booking/**", "/api/v1/bookings")
        .build();
  }

  private static final String[] BOOKING_V2_DECOMMISSIONED_OPENAPI_PATHS = {
      "/api/v2/bookings",
      "/api/v2/bookings/{bookingId}"
  };

  @Bean
  GroupedOpenApi bookingV2Api() {
    return GroupedOpenApi.builder()
        .group("booking-v2")
        .pathsToMatch("/api/v2/bookings/**")
        .pathsToExclude(BOOKING_V2_DECOMMISSIONED_OPENAPI_PATHS)
        .addOpenApiCustomiser(new BookingV2DecommissionedOpenApiCustomiser())
        .addOpenApiCustomiser(new BookingV2OpenApiCustomiser())
        .build();
  }

  @Bean
  GroupedOpenApi bookingV3Api() {
    return GroupedOpenApi.builder()
        .group("booking-v3")
        .displayName("ECOM API - Booking (V3)")
        .pathsToMatch("/api/v3/bookings/**")
        .build();
  }

  @Bean
  GroupedOpenApi summaryApi() {
    return GroupedOpenApi.builder()
        .group("summary")
        .pathsToMatch("/api/v1/summary/**")
        .build();
  }

  @Bean
  GroupedOpenApi siblApi() {
    return GroupedOpenApi.builder()
        .group("sibl")
        .pathsToMatch("/api/v1/bill-of-lading/**")
        .build();
  }

  @Bean
  GroupedOpenApi registrationApi() {
    return GroupedOpenApi.builder()
        .group("registration")
        .pathsToMatch("/api/v1/ecom/**", "/api/v1/register/**")
        .build();
  }



  @Bean
  GroupedOpenApi accountApi() {
    return GroupedOpenApi.builder()
        .group("account")
        .pathsToMatch("/api/v1/account/**")
        .build();
  }

  @Bean
  GroupedOpenApi ratesApi() {
    return GroupedOpenApi.builder()
        .group("rates")
        .displayName("ECOM API - Rates")
        .pathsToMatch("/api/v1/rates/**")
        .build();
  }

  @Bean
  GroupedOpenApi contractV2Api() {
    return GroupedOpenApi.builder()
        .group("contract-v2")
        .displayName("ECOM API - Contract (V2)")
        .pathsToMatch("/api/v2/contracts", "/api/v2/contracts/**")
        .addOpenApiCustomiser(new ContractV2OpenApiCustomiser())
        .build();
  }

  @Bean
  GroupedOpenApi billOfLadingV2Api() {
    return GroupedOpenApi.builder()
        .group("bill-of-lading-v2")
        .displayName("ECOM API - Bill of Lading (V2)")
        .pathsToMatch("/api/v2/billOfLading/**")
        .addOpenApiCustomiser(new BillOfLadingV2OpenApiCustomiser())
        .build();
  }

  // ---- PRIVATE GROUP ----
  @Bean
  GroupedOpenApi customerPrivateApi() {
    return GroupedOpenApi.builder()
        .group("customer-private")
        .pathsToMatch(
            "/api/v1/booking/**",
            "/api/v2/bookings/**",
            "/api/v3/bookings/**",
            "/api/v1/summary/**", 
            "/api/v1/bill-of-lading/**",
            "/api/v2/billOfLading/**",
            "/api/v1/ecom/**",
            "/api/v1/register/**",
            "/api/v1/account/**",
            "/api/v1/rates/**",
            "/api/v2/contracts/**")
        .pathsToExclude(CUSTOMER_PRIVATE_EXCLUDED_PATHS)
        .addOpenApiCustomiser(new BookingV2DecommissionedOpenApiCustomiser())
        .build();
  }

  // Decommissioned booking v2 paths + SYSUSER-only system-integration endpoint (IAD TSHG-2935 MRG list),
  // which must not appear in customer-facing docs.
  private static final String[] CUSTOMER_PRIVATE_EXCLUDED_PATHS = {
      BOOKING_V2_DECOMMISSIONED_OPENAPI_PATHS[0],
      BOOKING_V2_DECOMMISSIONED_OPENAPI_PATHS[1],
      "/api/v1/rates/mrg"
  };
}
