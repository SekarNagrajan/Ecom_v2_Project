
package com.svm.ecom.security;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// Import standardized error response schemas
import com.svm.ecom.dto.ErrorResponse;



@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication API", description = "Secure login and JWT token issuance endpoints. Use the returned token with 'Bearer <token>' format in Authorization header for protected endpoints.")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenSessionManager tokenSessionManager;

    @PostMapping("/login")
    @Operation(
        summary = SwaggerDocs.Auth.SUMMARY_LOGIN,
        description = SwaggerDocs.Auth.DESC_LOGIN,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    schema = @Schema(implementation = LoginResponse.class),
                    examples = @ExampleObject(
                        name = "Success",
                        value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                    )
                )
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
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_FORBIDDEN
                    )
                )
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
            ),
            @ApiResponse(
                responseCode = "503",
                description = SwaggerDocs.Common.SERVICE_UNAVAILABLE_503,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "ServiceUnavailable",
                        value = SwaggerDocs.Examples.ERROR_SERVICE_UNAVAILABLE
                    )
                )
            )
        }
    )
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

        // Authenticate user - will throw exception if credentials are invalid.
        // Role is loaded once here via CustomUserDetailsService (ecomapi_users) and embedded in the JWT.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String role = extractRoleFromAuthentication(authentication);
        String token = jwtUtil.generateToken(request.getUsername(), role);
        // Generate refresh token for future use but don't include in response
        @SuppressWarnings("unused")
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
        // TODO: Store refreshToken for future refresh functionality

        // Register token in session manager for security tracking
        String tokenSignature = tokenSessionManager.extractTokenSignature(token);
        tokenSessionManager.registerToken(request.getUsername(), tokenSignature);

        LoginResponse response = new LoginResponse(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Hidden
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        
        try {
            // Extract token from Authorization header
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return ResponseEntity.badRequest().body(new LogoutResponse("400", "Missing or invalid Authorization header"));
            }

            // Extract username from token
            String username = jwtUtil.extractUsername(token);
            
            // Invalidate the specific token
            String tokenSignature = tokenSessionManager.extractTokenSignature(token);
            boolean invalidated = tokenSessionManager.invalidateToken(username, tokenSignature);
            
            if (invalidated) {
                return ResponseEntity.ok(new LogoutResponse("200", "Successfully logged out"));
            } else {
                return ResponseEntity.badRequest().body(new LogoutResponse("400", "Token was not found in active sessions"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new LogoutResponse("500", "Error during logout: " + e.getMessage()));
        }
    }

    /**
     * Extract JWT token from Authorization header
     * @param authorizationHeader the Authorization header value
     * @return the JWT token without "Bearer " prefix, or null if invalid
     */
    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    /**
     * Reads role from Spring Security authorities already loaded at login (ROLE_* prefix stripped).
     * Avoids a second database lookup after authentication.
     */
    private static String extractRoleFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return null;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority == null || authority.getAuthority() == null) {
                continue;
            }
            String auth = authority.getAuthority();
            if (auth.startsWith("ROLE_") && auth.length() > 5) {
                return auth.substring(5);
            }
        }
        return null;
    }
}
