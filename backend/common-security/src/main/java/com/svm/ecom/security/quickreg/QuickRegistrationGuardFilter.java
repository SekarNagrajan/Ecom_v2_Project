package com.svm.ecom.security.quickreg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.dto.ErrorMessage;
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.dto.NestedErrorMessage;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import com.svm.ecom.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Guards write (POST / PUT) APIs from being executed by users whose
 * registration is still incomplete ("Quick Registration" users, status = 'D'
 * in {@code ecomuserdetails}).
 *
 * <p>Runs immediately after {@link com.svm.ecom.security.JwtAuthenticationFilter}
 * so that the authenticated principal (email ID) is available from
 * {@link SecurityContextHolder}. Read-only requests (GET / HEAD / OPTIONS /
 * DELETE / PATCH), authentication / registration / password / Swagger
 * endpoints, and non-{@code REQUEST} dispatches (ERROR / ASYNC / FORWARD /
 * INCLUDE) are skipped.
 *
 * <p>Block rules (per business spec):
 * <ul>
 *   <li>{@code status = 'D'} → block with HTTP 403</li>
 *   <li>{@code status = 'A'} → allow</li>
 *   <li>{@code status} null / user not found → block (fail-secure)</li>
 *   <li>any other status → allow (handled by other layers if needed)</li>
 * </ul>
 *
 * <p>Errors are written directly to the response using the same V1
 * ({@link ErrorResponse}) / V2 ({@link ErrorMessage}) dual-shape pattern used by
 * {@code RestAuthenticationEntryPoint}, so no
 * {@code @RestControllerAdvice} change is needed (filter exceptions are not
 * caught by {@code DispatcherServlet}).
 */
@Component
public class QuickRegistrationGuardFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(QuickRegistrationGuardFilter.class);

    /** Status value that marks a Quick Registration / incomplete user. */
    private static final String STATUS_INCOMPLETE_REGISTRATION = "D";

    /** User-facing message returned when the request is blocked. */
    private static final String BLOCK_MESSAGE =
            "Please complete the registration process before performing this action.";

    /**
     * Paths that must remain reachable for incomplete-registration users so
     * they can finish onboarding / re-authenticate / browse documentation.
     * Mirrors the {@code permitAll(...)} list in {@code SecurityConfig} plus
     * the registration / password / refresh endpoints exposed by
     * {@code register-service}.
     */
    private static final List<String> SKIP_PATH_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            // --- Authentication (common-security AuthController) ---
            "/api/v1/auth/**",
            "/ecomapi-service/api/v1/auth/**",

            // --- Registration / login / token / password (register-service) ---
            "/api/v1/ecom/register",
            "/api/v1/ecom/quick-register",
            "/api/v1/ecom/auth/login",
            "/api/v1/ecom/refresh",
            "/api/v1/ecom/forgot-password",
            "/api/v1/ecom/reset-password",
            "/ecomapi-service/api/v1/ecom/register",
            "/ecomapi-service/api/v1/ecom/quick-register",
            "/ecomapi-service/api/v1/ecom/auth/login",
            "/ecomapi-service/api/v1/ecom/refresh",
            "/ecomapi-service/api/v1/ecom/forgot-password",
            "/ecomapi-service/api/v1/ecom/reset-password",

            // --- Swagger / OpenAPI ---
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-auth/**",
            "/swagger-test/**",
            "/swagger-ui-custom.js",
            "/v2/api-docs/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/customer/**",
            "/company/**",
            "/public/swagger-ui.html",
            "/ecomapi-service/swagger-ui/**",
            "/ecomapi-service/swagger-ui.html",
            "/ecomapi-service/swagger-resources",
            "/ecomapi-service/swagger-resources/**",
            "/ecomapi-service/swagger-auth/**",
            "/ecomapi-service/swagger-test/**",
            "/ecomapi-service/swagger-ui-custom.js",
            "/ecomapi-service/v2/api-docs/**",
            "/ecomapi-service/v3/api-docs/**",
            "/ecomapi-service/webjars/**",
            "/ecomapi-service/customer/**",
            "/ecomapi-service/company/**",
            "/ecomapi-service/public/swagger-ui.html"
    ));

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final UserStatusService userStatusService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public QuickRegistrationGuardFilter(UserStatusService userStatusService,
                                        JwtUtil jwtUtil,
                                        ObjectMapper objectMapper) {
        this.userStatusService = userStatusService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * Skip the guard entirely for read-only methods, non-{@code REQUEST}
     * dispatches, whitelisted paths and unauthenticated requests (those are
     * handled by Spring Security's authentication / authorization layers).
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Only POST and PUT are subject to this validation.
        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method)) {
            return true;
        }

        // Skip ERROR / ASYNC / FORWARD / INCLUDE dispatches.
        if (request.getDispatcherType() != DispatcherType.REQUEST) {
            return true;
        }

        // Skip whitelisted paths (auth / registration / password / refresh / swagger).
        String path = request.getRequestURI();
        if (path != null) {
            for (String pattern : SKIP_PATH_PATTERNS) {
                if (pathMatcher.match(pattern, path)) {
                    return true;
                }
            }
        }

        // If no authenticated principal is present, let Spring Security reject
        // the request via its standard ExceptionTranslationFilter chain.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null || auth.getName().isEmpty()) {
            return true;
        }

        // Internal-auth tokens (ecomapi_users) are not subject to ecomuserdetails registration rules.
        String token = extractBearerToken(request.getHeader("Authorization"));
        if (token != null && !jwtUtil.isEcomCustomerToken(token)) {
            return true;
        }

        return false;
    }

    private static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String status = userStatusService.findStatusByEmail(email);

        boolean blockBecauseIncomplete = STATUS_INCOMPLETE_REGISTRATION.equalsIgnoreCase(status);
        boolean blockBecauseUnknown = (status == null);

        if (blockBecauseIncomplete || blockBecauseUnknown) {
            logger.info("Blocking {} {} for emailid='{}' (status={})",
                    request.getMethod(), request.getRequestURI(), email,
                    status == null ? "<not-found>" : status);
            writeForbiddenResponse(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Write the block response using the project's standard V1 / V2 error
     * shapes (same pattern as {@code RestAuthenticationEntryPoint}).
     */
    private void writeForbiddenResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpStatus status = HttpStatus.FORBIDDEN;
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String path = request.getRequestURI();
        if (isV2Path(path)) {
            ErrorMessage body = new ErrorMessage();
            body.setDetail(BLOCK_MESSAGE);
            body.setTitle(status.getReasonPhrase());
            body.setStatus(status.value());
            body.setInstance(path);
            body.setCode(String.valueOf(ErrorCodes.AUTH_QUICK_REGISTRATION_INCOMPLETE));
            body.setErrors(Collections.singletonList(NestedErrorMessage.of(
                    BLOCK_MESSAGE, null, String.valueOf(ErrorCodes.AUTH_QUICK_REGISTRATION_INCOMPLETE))));
            objectMapper.writeValue(response.getOutputStream(), body);
        } else {
            ErrorResponse body = new ErrorResponse(
                    status.value(),
                    status.getReasonPhrase(),
                    LocalDateTime.now()
            );
            body.addError(ErrorCodes.AUTH_QUICK_REGISTRATION_INCOMPLETE, null, null,
                    ErrorReason.FORBIDDEN, BLOCK_MESSAGE);
            objectMapper.writeValue(response.getOutputStream(), body);
        }
    }

    private boolean isV2Path(String path) {
        return path != null && (path.contains("/api/v2/") || path.contains("/ecomapi-service/api/v2/"));
    }
}
