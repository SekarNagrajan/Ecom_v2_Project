package com.svm.ecom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public RestAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        String jwtError = (String) request.getAttribute("JWT_ERROR");
        String errorMessage = jwtError != null ? jwtError : authException.getMessage();

        response.setStatus(status.value());
        response.setContentType("application/json");

        String path = request.getRequestURI();
        if (isV2Path(path)) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("detail", errorMessage);
            body.put("title", status.getReasonPhrase());
            body.put("status", status.value());
            body.put("instance", path);
            body.put("code", "UNAUTHORIZED");
            mapper.writeValue(response.getOutputStream(), body);
        } else {
            ErrorResponse body = new ErrorResponse(
                    status.value(),
                    status.getReasonPhrase(),
                    LocalDateTime.now()
            );
            body.addError(ErrorCodes.UNAUTHORIZED, null, null, ErrorReason.UNAUTHORIZED, errorMessage);
            mapper.writeValue(response.getOutputStream(), body);
        }
    }

    private boolean isV2Path(String path) {
        return path != null && (path.contains("/api/v2/") || path.contains("/ecomapi-service/api/v2/"));
    }
}