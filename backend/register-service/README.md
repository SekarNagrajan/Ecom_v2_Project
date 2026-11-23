# Register Service Module

A library JAR module for user registration, authentication, and profile management in the ECOM monorepo.

## Overview

This module provides:
- User registration endpoints
- Authentication with JWT tokens
- Account lockout mechanism
- Password management (forgot/change)
- User profile management
- Refresh token handling

## Features

### Endpoints
- `POST ${ecom.api.base-path}/ecom/customer/auth/register` - Register new user
- `POST ${ecom.api.base-path}/ecom/customer/auth/validate` - Validate JWT token from Authorization header
- `POST ${ecom.api.base-path}/ecom/customer/auth/validate-token` - Validate JWT token from request body
- `POST ${ecom.api.base-path}/ecom/customer/auth/refresh` - Refresh access token
- `POST ${ecom.api.base-path}/ecom/customer/auth/forgot-password` - Initiate password reset
- `POST ${ecom.api.base-path}/ecom/customer/auth/change-password` - Change password
- `GET ${ecom.api.base-path}/ecom/customer/auth/profile` - Get user profile (authenticated)

**Note**: Login functionality is provided by the common-security module at `POST ${ecom.api.base-path}/auth/login`

### Database Integration
- Uses existing `ecomuserdetails` table in MS SQL database
- Supports both legacy plain text and BCrypt password storage
- Automatic migration to BCrypt for new passwords

### Security Features
- Account lockout after configurable failed attempts
- JWT access tokens with custom claims
- Secure refresh tokens (64-char hex)
- Session tracking via TokenSessionManager
- Password verification with backward compatibility

## Configuration

The module reads configuration from the api-gateway's `application.properties`:

```properties
# API base path
ecom.api.base-path=/api/v1

# Authentication settings
ecom.auth.maxWrongAttempts=5
ecom.auth.lockMinutes=30
ecom.auth.accessTtlSeconds=300

# Registration defaults
ecom.register.default-status=A
ecom.register.default-timezone=Asia/Kolkata
```

## Database Schema

The module expects the `ecomuserdetails` table with these columns:
- `Id` (int, PK, identity)
- `EmailID` (varchar(150), NOT NULL)
- `Password` (varchar(max), NOT NULL)
- `userRole` (char(1), NOT NULL)
- `CustomerCode` (varchar(20), NOT NULL)
- `Status` (char(1), NOT NULL) - 'A' or '1' for active
- `preferredTimezone` (varchar(150), NOT NULL)
- `apptoken` (varchar(100), NOT NULL) - refresh token storage
- `WrgPassAtmpt` (int, NULL) - wrong password attempts
- `LockedStatus` (char(1), NOT NULL) - 'Y'/'N'
- `LockedTime` (datetime, NULL)
- Additional audit fields...

## Dependencies

- Spring Boot Web, Validation, Security, Data JPA
- common-security module (for JwtUtil, TokenSessionManager)
- MS SQL JDBC driver
- Lombok for boilerplate reduction

## Integration

This library JAR is included in the api-gateway application. The gateway's Spring Boot application scans both `com.svm.ecom` and `com.solverminds.ecom` packages to pick up all components.

## Error Handling

Standard error responses:
- `400` - Validation errors (ECOM-VALIDATION)
- `401` - Authentication failures (ECOM-AUTH-FAILED, ECOM-AUTH-LOCKED, ECOM-AUTH-INACTIVE)
- `409` - Duplicate registration (ECOM-REGISTER-DUP)
- `500` - Server errors (ECOM-SERVER)
