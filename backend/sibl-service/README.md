# SIBL Service - OpenAPI v3.0 Implementation

SIBL (Shipping Instruction and Bill of Lading) Service provides REST APIs for accessing SI and BL summary data with comprehensive OpenAPI v3.0 documentation.

## 🚀 Features

- **OpenAPI v3.0 Compliant**: Full compliance with OpenAPI v3.0 standards
- **Comprehensive Documentation**: Detailed API documentation with examples
- **JWT Authentication**: Secure token-based authentication
- **Native Queries**: Direct stored procedure execution
- **Type Safety**: Strongly typed DTOs with validation
- **Error Handling**: Standardized error responses

## 📋 API Overview

### Base URL
The service runs on port `8081` and all endpoints are under `/api/bill-of-lading`

### OpenAPI v3.0 Standards Implemented

#### 1. **Detailed Operation Documentation**
- ✅ Comprehensive summaries and descriptions
- ✅ Proper tagging and categorization
- ✅ Parameter documentation with examples
- ✅ Response schema definitions

#### 2. **Schema Definitions**
- ✅ Strongly typed DTOs with OpenAPI annotations
- ✅ Field descriptions and examples
- ✅ Validation constraints
- ✅ Enumeration values where applicable

#### 3. **Response Documentation**
- ✅ Success response schemas
- ✅ Error response schemas  
- ✅ HTTP status code definitions
- ✅ Content type specifications
- ✅ Example responses

#### 4. **Security Documentation**
- ✅ JWT Bearer token authentication
- ✅ Security scheme definitions
- ✅ Header parameter documentation

## 🔗 API Endpoints

### 1. **Shipping Instruction Summary**
```
GET /api/bill-of-lading/sisummary
```
- **Description**: Retrieves SI summary data via `exec [usp_ecomSISummaryNew]`
- **Authentication**: JWT Bearer token required
- **Response**: `ApiResponse<SiSummaryDto>`

### 2. **Bill of Lading Summary**
```
GET /api/bill-of-lading/blsummary
```
- **Description**: Retrieves BL summary data via `exec [usp_EcomBLSummary]`
- **Parameters**:
  - `types` (optional): Default 'AP,BP'
  - `flag` (optional): Default 'N' 
- **Authentication**: JWT Bearer token required
- **Response**: `ApiResponse<BlSummaryDto>`

## 📊 DTO Structure

### **ApiResponse<T>**
```json
{
  "type": "siSummary",
  "customerCode": "20001374", 
  "data": [...],
  "timestamp": "2024-01-15T10:30:00",
  "totalRecords": 25,
  "success": true
}
```

### **SiSummaryDto**
```json
{
  "id": "SI001",
  "description": "Container shipping instruction summary",
  "siNumber": "SI20240115001",
  "customerReference": "CUST-REF-001",
  "bookingNumber": "BKG20240115001",
  "containerNumber": "MSKU1234567",
  "vesselName": "EVER GIVEN",
  "voyageNumber": "001N",
  "portOfLoading": "INMAA",
  "portOfDischarge": "SGSIN",
  "status": "ACTIVE",
  "createdDate": "2024-01-15"
}
```

### **BlSummaryDto**
```json
{
  "id": "BL001",
  "description": "Bill of Lading summary",
  "blNumber": "BL20240115001",
  "masterBlNumber": "MBL20240115001",
  "houseBlNumber": "HBL20240115001",
  "shipperName": "ABC Exports Ltd",
  "consigneeName": "XYZ Imports Ltd",
  "vesselName": "EVER GIVEN",
  "voyageNumber": "001N",
  "portOfLoading": "INMAA",
  "portOfDischarge": "SGSIN",
  "blType": "AP",
  "status": "ACTIVE",
  "issueDate": "2024-01-15"
}
```

### **ErrorResponse**
```json
{
  "error": "reportdb unavailable",
  "errorCode": "SERVICE_UNAVAILABLE",
  "status": 503,
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/bill-of-lading/sisummary"
}
```

## 🛠️ Development

### **Prerequisites**
- Java 8+
- Maven 3.6+
- SQL Server database with required stored procedures

### **Running the Service**
```bash
mvn spring-boot:run
```

### **API Documentation**
Access comprehensive Swagger UI documentation at: 
**`http://localhost:8081/swagger-ui.html`**

## 🗄️ Database Configuration

### **Required Stored Procedures**
1. **`usp_ecomSISummaryNew`**: For SI Summary data
   ```sql
   exec [usp_ecomSISummaryNew] '20001374'
   ```

2. **`usp_EcomBLSummary`**: For BL Summary data
   ```sql
   exec [usp_EcomBLSummary] '20001374','AP,BP','N'
   ```

### **Database Connection**
- **Database**: ReportDB  
- **Connection**: JDBC with HikariCP connection pooling
- **Security**: Customer-specific data filtering via JWT claims

## 🔒 Security

- **Authentication**: JWT Bearer tokens
- **Authorization**: Customer code extracted from JWT claims
- **Session Validation**: Optional session tracking for enhanced security
- **HTTPS**: Recommended for production environments

## 📚 OpenAPI v3.0 Compliance Features

### **✅ Implemented Standards**

1. **Info Object**: Complete API metadata
2. **Server Objects**: Multiple environment configurations  
3. **Path Objects**: Detailed endpoint documentation
4. **Operation Objects**: Comprehensive operation details
5. **Parameter Objects**: Query/header parameter documentation
6. **Response Objects**: Success and error response schemas
7. **Schema Objects**: Strongly typed data models
8. **Security Objects**: JWT authentication scheme
9. **Example Objects**: Request/response examples
10. **External Documentation**: Links to additional resources

### **🎯 Benefits**

- **Developer Experience**: Rich, interactive API documentation
- **Type Safety**: Strongly typed request/response models
- **Validation**: Automatic request/response validation
- **Code Generation**: Support for client SDK generation
- **Testing**: Comprehensive API testing via Swagger UI
- **Standardization**: Industry-standard API documentation

## 🔧 Configuration

Update database connection details in `application.properties`:

```properties
# Database Configuration (reportdb)
spring.datasource.report.url=jdbc:sqlserver://localhost:1433;databaseName=reportdb;trustServerCertificate=true
spring.datasource.report.username=your_username
spring.datasource.report.password=your_password
```

## 📈 Integration

The service integrates seamlessly with the API Gateway and follows the same patterns as other ECOMAPI services:

- **Component Scanning**: Automatic discovery via `@SpringBootApplication`
- **Security**: Common security module integration
- **Logging**: Comprehensive logging configuration
- **Error Handling**: Standardized error response format