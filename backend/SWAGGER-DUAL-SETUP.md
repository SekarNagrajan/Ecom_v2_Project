# Dual Swagger UI Configuration - Profile-Based Groups

This project supports two separate Swagger UI interfaces with explicit group configuration to prevent auto-discovery conflicts.

## Main Swagger UI

**URL:** `/ecomapi-service/swagger-ui.html`

**Available:** Always (default profile)

**Endpoints Included (Common APIs only):**
- **Schedule** - `/api/v1/schedule/**`
- **Tracking** - `/trackingendpoint/v1/**`
- **Master Data** - `/api/v1/master/**` 
- **Authentication** - `/api/v1/auth/**`

**Explicit Groups Configured:**
- `ECOM API - Schedule` → `/v3/api-docs/schedule`
- `ECOM API - Tracking` → `/v3/api-docs/tracking`
- `ECOM API - Master` → `/v3/api-docs/master`
- `ECOM API - Authenticate` → `/v3/api-docs/authenticate`

## Company Swagger UI

**URL:** `/ecomapi-service/company/swagger-ui.html`

**Available:** Only when `spring.profiles.active=company`

**Endpoints Included:**
- **Company Private APIs** - All `/api/v1/**` EXCEPT common APIs
  - `/api/v1/booking/**` - Booking summaries
  - `/api/v1/summary/**` - Dashboard and summary data
  - `/api/v1/register/**` - Registration endpoints
  - `/api/v1/sibl/**` - SIBL-specific endpoints
- **Plus all common APIs** (also available in dropdown)

**Explicit Groups Configured:**
- `ECOM API - Company (All Endpoints)` → `/v3/api-docs/company-private`
- `ECOM API - Schedule` → `/v3/api-docs/schedule`
- `ECOM API - Tracking` → `/v3/api-docs/tracking`
- `ECOM API - Master` → `/v3/api-docs/master`
- `ECOM API - Authenticate` → `/v3/api-docs/authenticate`

## Configuration Architecture

### Core Configuration Files

#### **1. OpenApiGroupsConfig.java**
Defines all API groups with explicit path matching:
```java
@Configuration
public class OpenApiGroupsConfig {
  // Common groups (always available)
  @Bean GroupedOpenApi scheduleApi() { ... }
  @Bean GroupedOpenApi trackingApi() { ... }
  @Bean GroupedOpenApi masterApi() { ... }
  @Bean GroupedOpenApi authApi() { ... }
  
  // Company-specific group (only when company profile active)
  @Bean @Profile("company") GroupedOpenApi companyPrivateApi() { ... }
}
```

#### **2. CompanySwaggerUiRedirect.java**
Handles company URL routing:
```java
@Controller
@Profile("company")
public class CompanySwaggerUiRedirect {
  @GetMapping("/company/swagger-ui.html")
  public String companySwaggerUi() {
    return "redirect:/swagger-ui.html?urls.primaryName=ECOM API - Company (All Endpoints)";
  }
}
```

### Properties Configuration

#### **3. application.properties (Main/Default)**
```properties
# Explicit list prevents auto-discovery
springdoc.swagger-ui.disable-swagger-default-url=true
springdoc.swagger-ui.urls[0].name=ECOM API - Schedule
springdoc.swagger-ui.urls[0].url=/v3/api-docs/schedule
springdoc.swagger-ui.urls[1].name=ECOM API - Tracking
springdoc.swagger-ui.urls[1].url=/v3/api-docs/tracking
springdoc.swagger-ui.urls[2].name=ECOM API - Master
springdoc.swagger-ui.urls[2].url=/v3/api-docs/master
springdoc.swagger-ui.urls[3].name=ECOM API - Authenticate
springdoc.swagger-ui.urls[3].url=/v3/api-docs/authenticate
```

#### **4. application-company.properties (Company Profile)**
```properties
# Company profile includes all groups
springdoc.swagger-ui.urls[0].name=ECOM API - Company (All Endpoints)
springdoc.swagger-ui.urls[0].url=/v3/api-docs/company-private
springdoc.swagger-ui.urls[1].name=ECOM API - Schedule
springdoc.swagger-ui.urls[1].url=/v3/api-docs/schedule
# ... (all other common groups)
```

## Development Usage

### **Default Mode (Main Swagger Only)**
```bash
mvn spring-boot:run
# Access: http://localhost:8080/swagger-ui.html
# Shows: 4 groups (Schedule, Tracking, Master, Authenticate)
```

### **Company Mode (Both UIs)**
```bash
mvn spring-boot:run -Dspring.profiles.active=company
# Main: http://localhost:8080/swagger-ui.html (4 common groups)
# Company: http://localhost:8080/company/swagger-ui.html (5 groups - company + common)
```

### **UAT Deployment**
```bash
# For UAT with company access
java -jar ecomapi-service.war -Dspring.profiles.active=uat,company
```

### **Production Deployment**
```bash
# Production only gets common APIs
java -jar ecomapi-service.war -Dspring.profiles.active=prod
# Only /swagger-ui.html available (4 common groups)
# /company/swagger-ui.html returns 404 (CompanySwaggerUiRedirect not loaded)
```

## Key Features

### **1. No Auto-Discovery Conflicts**
- `springdoc.swagger-ui.disable-swagger-default-url=true`
- Explicit `urls[]` configuration prevents SpringDoc from auto-discovering all groups
- Each profile controls exactly which groups appear in dropdown

### **2. Profile-Based Security**
- Company-specific controller only loads with `@Profile("company")`
- Company-private group only creates with `@Profile("company")`
- Production deployments automatically exclude company features

### **3. Clean Separation**
- Common APIs always available at main UI
- Company APIs isolated to separate group and URL
- No mixing of public/private endpoints in same group

### **4. Path Strategy**
- **Company-private group**: Includes all `/api/v1/**` EXCEPT common paths
- **Common groups**: Explicit path matching for each service area
- **Tracking**: Uses current `/trackingendpoint/v1/**` path

## Environment Behavior

| Environment | Profile | Main UI | Company UI | Groups in Main | Groups in Company |
|-------------|---------|---------|------------|----------------|-------------------|
| **Development** | `default` | ✅ | ❌ | 4 common | N/A |
| **Development** | `company` | ✅ | ✅ | 4 common | 5 (company + common) |
| **UAT** | `uat` | ✅ | ❌ | 4 common | N/A |
| **UAT** | `uat,company` | ✅ | ✅ | 4 common | 5 (company + common) |
| **Production** | `prod` | ✅ | ❌ | 4 common | N/A |

## Technical Notes

- Uses springdoc-openapi 1.7.0 (Spring Boot 2.7.18 + Java 8 compatible)
- Explicit URL configuration prevents auto-discovery dropdown pollution
- Profile-based beans ensure clean separation without conflicts
- Legacy swagger controllers disabled to prevent path conflicts
- Company redirect controller provides user-friendly `/company/swagger-ui.html` URL