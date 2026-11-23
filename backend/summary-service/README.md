# Summary Service

A Spring Boot 2.7.18 microservice that provides summary data endpoints with JWT authentication.

## Features

- **JWT Authentication**: Uses `customerCode` claim from JWT tokens
- **Database Integration**: JdbcTemplate bound to `reportdb` 
- **Health Monitoring**: Spring Boot Actuator health endpoint
- **Error Handling**: Returns 503 if reportdb unavailable

## REST Endpoints

Base URL: `/api/summary`

### 🔐 All endpoints require JWT authentication via `Authorization: Bearer <token>`

| Endpoint | Method | Description | Parameters |
|----------|--------|-------------|------------|
| `/dashboard` | GET | Dashboard booking summary | None |
| `/cro` | GET | CRO details | `from=YYYY-MM-DD`, `to=YYYY-MM-DD` |
| `/arrival-notice` | GET | Arrival notice data (placeholder) | `from=YYYY-MM-DD`, `to=YYYY-MM-DD` |
| `/delivery-order` | GET | Delivery order data (placeholder) | `from=YYYY-MM-DD`, `to=YYYY-MM-DD` |

### Health Check
- `/actuator/health` - Available without authentication

## Database Stored Procedures

- **Dashboard**: `exec usp_getdashboardecomboookingsummary ?, '', '', 'N', ''`
- **CRO**: `exec usp_ecomcrodetails ?, ?, ?, '', ''`
- **Arrival Notice**: `exec usp_ecomarrivalnotice ?, ?, ?, '', ''` *(placeholder)*
- **Delivery Order**: `exec usp_ecomdeliveryorder ?, ?, ?, '', ''` *(placeholder)*

## Configuration

Update `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.report.url=jdbc:sqlserver://your-server:1433;databaseName=reportdb;trustServerCertificate=true
spring.datasource.report.username=your_username
spring.datasource.report.password=your_password
```

## Build & Run

```bash
mvn clean install
mvn spring-boot:run
```

The service runs on port 8080 by default.

## JWT Requirements

- **Header**: `Authorization: Bearer <jwt-token>`
- **Required Claim**: `customerCode` - extracted and used for database queries
- **Validation**: Full JWT signature and session validation via `common-security`

## Error Responses

- **401 Unauthorized**: Missing/invalid JWT token or missing `customerCode` claim
- **400 Bad Request**: Invalid date parameters (`from` > `to`)
- **503 Service Unavailable**: `reportdb` not configured or unavailable
- **500 Internal Server Error**: Unexpected server errors

## Example Usage

```bash
# Dashboard
curl -H "Authorization: Bearer <jwt>" http://localhost:8080/api/summary/dashboard

# CRO Report  
curl -H "Authorization: Bearer <jwt>" \
     "http://localhost:8080/api/summary/cro?from=2024-01-01&to=2024-01-31"

# Health Check (no auth required)
curl http://localhost:8080/actuator/health
```

## Dependencies

- Spring Boot 2.7.18
- Java 8
- SQL Server JDBC Driver
- HikariCP Connection Pooling  
- Spring Security + JWT via `common-security`
- Spring Boot Actuator
