package com.solverminds.ecom.register.controller;

import com.solverminds.ecom.register.dto.ChangePasswordRequest;
import com.solverminds.ecom.register.dto.ChangePasswordResponse;
import com.solverminds.ecom.register.service.ProfileService;
import com.solverminds.ecom.register.service.RegAuthService;
import com.solverminds.ecom.register.dto.*;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;
// Error response schema
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/account")
@Tag(name = "Account Management", description = "Account management operations for logged-in users")
public class AccountController {

    @Autowired
    private RegAuthService authService;

    @Autowired
    private ProfileService profileService;

    @PostMapping("/password/change")
    @Operation(
        summary = "Change password",
        description = "Change password for logged-in users with current password verification",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = ChangePasswordResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "422",
                description = SwaggerDocs.Common.UNPROCESSABLE_ENTITY_422,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody 
            @Parameter(description = "Change password request") ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Extract JWT token from Authorization header
            String authorizationHeader = httpRequest.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }
            
            // Call the secure change password service
            ChangePasswordResponse response = authService.secureChangePassword(request, token);
            return ResponseEntity.ok(response);
            
        } catch (SecurityException e) {
            // JWT validation failures
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            
            if ("ECOM-AUTH-FORBIDDEN".equals(errorMessage)) {
                // User status not 'A' or user not found
                return forbidden("User account is not active or not found");
            } else if ("ECOM-VALIDATION-MISMATCH".equals(errorMessage)) {
                // New password != confirm new password
                return badRequest("New password and confirm password do not match");
            } else if ("ECOM-AUTH-CURRENT-INVALID".equals(errorMessage)) {
                // Current password is incorrect
                return unprocessableEntity("Current password is incorrect");
            } else {
                // Other server errors
                return internalError("An error occurred while changing password: " + errorMessage);
            }
        } catch (Exception e) {
            return internalError("An unexpected error occurred while changing password");
        }
    }

    
    @GetMapping("/profile")
    @Operation(
        summary = "Get user profile",
        description = "Retrieves comprehensive profile information for the authenticated user including company details and contact information",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = ShapedProfileResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            // Extract JWT from Authorization header
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return unauthorized("Missing or invalid JWT token");
            }

            // Use the comprehensive token validation
            String email = authService.validateTokenAndExtractUsername(token);
            
            // Keep existing fetch logic exactly as-is
            ProfileDto profile = profileService.getProfile(email);
            
            // Transform response before returning
            ShapedProfileResponse shapedResponse = transformProfileResponse(profile);
            return ResponseEntity.ok(shapedResponse);

        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed");
        } catch (RuntimeException e) {
            if ("ECOM-AUTH-FAILED".equals(e.getMessage())) {
                return unauthorized("Authentication failed");
            }
            return internalError("An error occurred while retrieving profile: " + e.getMessage());
        } catch (Exception e) {
            return internalError("An unexpected error occurred while retrieving profile");
        }
    }

    /**
     * Transform ProfileDto to ShapedProfileResponse
     * Keeps existing fetch logic unchanged, only shapes the output
     */
    private ShapedProfileResponse transformProfileResponse(ProfileDto profile) {
        if (profile == null) {
            return null;
        }

        // Transform company details
        ShapedCompanyDetails shapedCompanyDetails = null;
        if (profile.getCompanyDetails() != null) {
            CompanyDto companyDto = profile.getCompanyDetails();
            shapedCompanyDetails = new ShapedCompanyDetails();
            
            // Rename custName to customerName and keep only specified fields
            shapedCompanyDetails.setCustomerName(companyDto.getCustName());
            shapedCompanyDetails.setAddress1(companyDto.getAddress1());
            shapedCompanyDetails.setAddress2(companyDto.getAddress2());
            shapedCompanyDetails.setCity(companyDto.getCity());
            shapedCompanyDetails.setCountryName(companyDto.getCountryName());
            shapedCompanyDetails.setEmail(companyDto.getEmail());
            shapedCompanyDetails.setPhone(companyDto.getPhone());
            shapedCompanyDetails.setWebsite(companyDto.getWebsite() != null ? companyDto.getWebsite() : "");
            shapedCompanyDetails.setPoBox(companyDto.getPoBox() != null ? companyDto.getPoBox() : "");
            shapedCompanyDetails.setCountryId(companyDto.getCountryId() != null ? companyDto.getCountryId() : "CN");
            shapedCompanyDetails.setZipCode(companyDto.getZipCode() != null ? companyDto.getZipCode() : "603103");
            shapedCompanyDetails.setState(companyDto.getState() != null ? companyDto.getState() : "");
            shapedCompanyDetails.setControlLocation(companyDto.getControlLocation() != null ? companyDto.getControlLocation() : "INMAA");
        }

        // Transform customer contacts - keep only specified fields
        List<ShapedCustomerContact> shapedCustomerContacts = null;
        if (profile.getCustomerContacts() != null) {
            shapedCustomerContacts = profile.getCustomerContacts().stream()
                .map(contact -> {
                    ShapedCustomerContact shapedContact = new ShapedCustomerContact();
                    shapedContact.setCountryId(contact.getCountryId());
                    shapedContact.setContactName(contact.getContactName());
                    shapedContact.setCustName(contact.getCustName());
                    shapedContact.setAddress1(contact.getAddress1());
                    shapedContact.setPhone(contact.getPhone());
                    shapedContact.setFax(contact.getFax());
                    shapedContact.setEmail(contact.getEmail());
                    shapedContact.setMobile(contact.getMobile());
                    shapedContact.setOnlineActive(contact.getOnlineActive());
                    shapedContact.setCity(contact.getCity());
                    shapedContact.setWebId(contact.getWebId());
                    return shapedContact;
                })
                .collect(Collectors.toList());
        }

        return new ShapedProfileResponse(shapedCompanyDetails, shapedCustomerContacts);
    }

    /**
     * Create a proper ErrorResponse for 401 Unauthorized errors
     */
    private ResponseEntity<ErrorResponse> unauthorized(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            401,
            "Unauthorized", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.UNAUTHORIZED,
            null,
            null,
            ErrorReason.UNAUTHORIZED,
            message != null ? message : "Authentication required or invalid token"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 403 Forbidden errors
     */
    private ResponseEntity<ErrorResponse> forbidden(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            403,
            "Forbidden", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.FORBIDDEN,
            null,
            null,
            ErrorReason.FORBIDDEN,
            message != null ? message : "You do not have permission to access this resource"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 400 Bad Request errors
     */
    private ResponseEntity<ErrorResponse> badRequest(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            400,
            "Bad Request", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.BAD_REQUEST,
            null,
            null,
            ErrorReason.BAD_REQUEST,
            message != null ? message : "Bad request parameters"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 422 Unprocessable Entity errors
     */
    private ResponseEntity<ErrorResponse> unprocessableEntity(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            422,
            "Unprocessable Entity", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.BUSINESS_RULE_VIOLATION,
            null,
            null,
            ErrorReason.BUSINESS_RULE,
            message != null ? message : "Request cannot be processed due to business rule violation"
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }

    /**
     * Create a proper ErrorResponse for 500 Internal Server Error
     */
    private ResponseEntity<ErrorResponse> internalError(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            500,
            "Internal Server Error", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.INTERNAL_ERROR,
            null,
            null,
            ErrorReason.INTERNAL_ERROR,
            message != null ? message : "An unexpected server error occurred"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
