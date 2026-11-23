package com.svm.ecom.security.shippeo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.dto.ErrorMessage;
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.dto.NestedErrorMessage;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import com.svm.ecom.security.JwtUtil;
import com.svm.ecom.security.UserApiPermissionService;
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
 * Dynamic API allowlist for internal-auth users ({@code /api/v1/auth/login}) that have
 * active rows ({@code access_flag = 'Y'}) in {@code ecomapi_user_api_permissions}. Ecom-login tokens
 * and users without permission rows bypass this filter unchanged.
 */
@Component
public class ShippeoApiAuthorizationGuardFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ShippeoApiAuthorizationGuardFilter.class);

    private static final String BLOCK_MESSAGE = "Access to this API is not permitted for your account.";

    private static final List<String> SKIP_PATH_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            "/api/v1/auth/**",
            "/ecomapi-service/api/v1/auth/**"
    ));

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;
    private final UserApiPermissionService userApiPermissionService;
    private final ObjectMapper objectMapper;

    public ShippeoApiAuthorizationGuardFilter(JwtUtil jwtUtil,
                                              UserApiPermissionService userApiPermissionService,
                                              ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userApiPermissionService = userApiPermissionService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (request.getDispatcherType() != DispatcherType.REQUEST) {
            return true;
        }

        String path = request.getRequestURI();
        if (matchesAny(path, SKIP_PATH_PATTERNS)) {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return true;
        }

        String token = extractBearerToken(request.getHeader("Authorization"));
        if (token == null) {
            return true;
        }

        if (jwtUtil.isEcomCustomerToken(token)) {
            return true;
        }

        String username = auth.getName();
        if (username == null || username.isEmpty() || "anonymousUser".equals(username)) {
            return true;
        }

        return !userApiPermissionService.hasEnabledPermissions(username);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (userApiPermissionService.isRequestAllowed(username, path, request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("Blocking {} {} for restricted internal-auth user '{}'",
                request.getMethod(), path, username);
        writeForbiddenResponse(request, response);
    }

    private boolean matchesAny(String requestUri, List<String> patterns) {
        if (requestUri == null) {
            return false;
        }
        for (String pattern : patterns) {
            if (pathMatcher.match(pattern, requestUri)) {
                return true;
            }
        }
        return false;
    }

    private static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

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
            body.setCode(String.valueOf(ErrorCodes.FORBIDDEN));
            body.setErrors(Collections.singletonList(NestedErrorMessage.of(
                    BLOCK_MESSAGE, null, String.valueOf(ErrorCodes.FORBIDDEN))));
            objectMapper.writeValue(response.getOutputStream(), body);
        } else {
            ErrorResponse body = new ErrorResponse(
                    status.value(),
                    status.getReasonPhrase(),
                    LocalDateTime.now()
            );
            body.addError(ErrorCodes.FORBIDDEN, null, null, ErrorReason.FORBIDDEN, BLOCK_MESSAGE);
            objectMapper.writeValue(response.getOutputStream(), body);
        }
    }

    private boolean isV2Path(String path) {
        return path != null && (path.contains("/api/v2/") || path.contains("/ecomapi-service/api/v2/"));
    }
}
