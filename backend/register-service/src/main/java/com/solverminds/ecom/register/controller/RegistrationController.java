package com.solverminds.ecom.register.controller;

import com.solverminds.ecom.register.dto.*;
import com.svm.ecom.api.registration.dto.RegistrationRequest;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import java.time.LocalDateTime;
import com.svm.ecom.api.registration.mapper.RegistrationMapper;
import com.svm.ecom.common.sequence.RegNoService;
import com.solverminds.ecom.register.service.RegAuthService;
import com.solverminds.ecom.register.service.PasswordService;
import com.solverminds.ecom.register.service.ProfileService;
import com.solverminds.ecom.register.service.QuickRegistrationService;
import com.solverminds.ecom.register.service.RegistrationService;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;
// Error response schema
import com.svm.ecom.dto.ErrorResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/ecom")
@Tag(name = "Registration & Authentication", description = "User registration, authentication and password management operations")
public class RegistrationController {

    @Autowired
    private RegAuthService authService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RegNoService regNoService;

    @Autowired
    private QuickRegistrationService quickRegistrationService;





    @PostMapping("/register")
    @Operation(
        summary = SwaggerDocs.Registration.SUMMARY_REGISTER_USER,
        description = SwaggerDocs.Registration.DESC_REGISTER_USER,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = RegistrationResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "SuccessfulRegistration",
                            value = "{\"success\": true, \"message\": \"Registration completed successfully\", \"regNo\": \"REG12345\", \"userId\": \"user@example.com\"}"
                        ),
                        @ExampleObject(
                            name = "DuplicateLoginName",
                            value = "{\"success\": true, \"message\": \"Login Name already exists\", \"regNo\": null, \"userId\": \"user@example.com\"}"
                        ),
                        @ExampleObject(
                            name = "InvalidCustomerCode",
                            value = "{\"success\": true, \"message\": \"Invalid Customer Code\", \"regNo\": null, \"userId\": \"user@example.com\"}"
                        ),
                        @ExampleObject(
                            name = "CompanyMismatch",
                            value = "{\"success\": true, \"message\": \"Company name mismatch for customer code\", \"regNo\": null, \"userId\": \"user@example.com\"}"
                        )
                    }
                )
            )
        }
    )
    public ResponseEntity<RegistrationResponse> register(
            @Valid @RequestBody 
            @Parameter(description = "User registration details") com.svm.ecom.api.registration.dto.RegistrationRequest request) {
        try {
            // Enrich request with server-side defaults before passing to service
            RegistrationRequest enrichedRequest = RegistrationMapper.enrich(request, regNoService.nextRegNo());
            RegistrationResponse response = registrationService.register(enrichedRequest);
            
            // Always return HTTP 200 with consistent JSON envelope
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            
            // Create an error response following the established pattern
            RegistrationResponse errorResponse = new RegistrationResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Registration failed due to an unexpected error: " + e.getMessage());
            errorResponse.setRegNo(null);
            errorResponse.setUserId(request != null ? request.getLoginName() : null);
            
            // Return HTTP 200 with error details in response body to maintain consistency
            return new ResponseEntity<>(errorResponse, HttpStatus.OK);
        }
    }

    @PostMapping("/quick-register")
    @Operation(
        summary = "Quick registration",
        description = "Creates a provisional user in ECOMUSERDETAILS with a system-generated dummy customer code (D-YYYY-NNNN).",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = QuickRegistrationResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "SuccessfulQuickRegistration",
                            value = "{\"success\": true, \"message\": \"Quick registration successful\", \"regNo\": \"REG12345\", \"userId\": \"user@example.com\"}"
                        ),
                        @ExampleObject(
                            name = "DuplicateEmail",
                            value = "{\"success\": true, \"message\": \"Login Name already exists\", \"regNo\": null, \"userId\": \"user@example.com\"}"
                        ),
                        @ExampleObject(
                            name = "WeakPassword",
                            value = "{\"success\": true, \"message\": \"Password does not meet complexity requirements\", \"regNo\": null, \"userId\": \"user@example.com\"}"
                        ),
                        @ExampleObject(
                            name = "InvalidEmailFormat",
                            value = "{\"success\": true, \"message\": \"Invalid email format\", \"regNo\": null, \"userId\": \"user@example.com\"}"
                        )
                    }
                )
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
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            )
        }
    )
    public ResponseEntity<QuickRegistrationResponse> quickRegister(
            @Valid @RequestBody
            @Parameter(description = "Quick registration details") QuickRegistrationRequest request) {
        try {
            QuickRegistrationResponse response = quickRegistrationService.quickRegister(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            QuickRegistrationResponse errorResponse = QuickRegistrationResponse.of(
                    false,
                    "Quick registration failed due to an unexpected error",
                    null,
                    request != null ? request.getEmailId() : null);
            return ResponseEntity.ok(errorResponse);
        }
    }

    @PostMapping("/auth/login")
    @Operation(
        summary = SwaggerDocs.Auth.SUMMARY_LOGIN,
        description = SwaggerDocs.Auth.DESC_LOGIN,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = RegLoginResponse.class))
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
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            )
        }
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<RegLoginResponse> login(
            @Valid @RequestBody 
            @Parameter(description = "Login credentials") RegLoginRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Step 1: Extract and validate JWT token from Authorization header
            String authorizationHeader = httpRequest.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                throw new SecurityException("Missing or invalid Authorization header");
            }

            // Step 2: Validate JWT token and extract username
            String tokenUsername = authService.validateTokenAndExtractUsername(token);
            
            // Step 3: Validate that the token username matches the request username
           // if (!tokenUsername.equalsIgnoreCase(request.getUsername())) {
              //  throw new SecurityException("Token username does not match request username");
         //   }
            
            // Step 4: Proceed with standard username/password validation
            RegLoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            // Return unauthorized for JWT validation failures
            e.printStackTrace();
            throw new RuntimeException("ECOM-AUTH-TOKEN-INVALID: " + e.getMessage());
            
        } catch (Exception e) {
            // Re-throw other exceptions (like ECOM-AUTH-FAILED from login service)
            throw e;
        }
    }

    @PostMapping("/refresh")
    @Operation(
        summary = SwaggerDocs.Auth.SUMMARY_REFRESH,
        description = SwaggerDocs.Auth.DESC_REFRESH,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = RegLoginResponse.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            )
        }
    )
    public ResponseEntity<RegLoginResponse> refresh(
            @Valid @RequestBody 
            @Parameter(description = "Refresh token request") RefreshTokenRequest request) {
        RegLoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(
        summary = SwaggerDocs.Registration.SUMMARY_RESET_PASSWORD,
        description = SwaggerDocs.Registration.DESC_RESET_PASSWORD,
        responses = {
            @ApiResponse(
                responseCode = "202",
                description = "Password reset email sent successfully"
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            )
        }
    )
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody 
            @Parameter(description = "Forgot password request") ForgotPasswordRequest request) {
        passwordService.forgotPassword(request);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/reset-password")
    @Operation(
        summary = SwaggerDocs.Registration.SUMMARY_RESET_PASSWORD_CONFIRM,
        description = SwaggerDocs.Registration.DESC_RESET_PASSWORD_CONFIRM,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(schema = @Schema(implementation = ResetPasswordResponse.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            )
        }
    )
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody 
            @Parameter(description = "Reset password request containing email and new password") ResetPasswordRequest request) {
        try {
            boolean result = passwordService.resetPassword(request);
            
            if (result) {
                return ResponseEntity.ok(new ResetPasswordResponse("Password reset successfully"));
            } else {
                return notFound("User not found");
            }
            
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            
            if ("ECOM-VALIDATION-MISMATCH".equals(errorMessage)) {
                return badRequest("New password and confirm password do not match");
            }
            throw e;
        }
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
     * Create a proper ErrorResponse for 404 Not Found errors
     */
    private ResponseEntity<ErrorResponse> notFound(String message) {
        ErrorResponse errorResponse = new ErrorResponse(
            404,
            "Not Found", 
            LocalDateTime.now()
        );
        errorResponse.addError(
            ErrorCodes.NOT_FOUND,
            null,
            null,
            ErrorReason.NOT_FOUND,
            message != null ? message : "Requested resource not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
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
