package com.svm.ecom.security.decommissioned;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.dto.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Blocks decommissioned REST endpoints before the dispatcher reaches controllers.
 * Uses the same V2 {@link ErrorMessage} JSON shape as Booking V2 APIs.
 */
@Component
public class DecommissionedEndpointGuardFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(DecommissionedEndpointGuardFilter.class);

    private static final String NOT_FOUND_DETAIL = "The requested resource could not be found.";
    private static final String NOT_FOUND_CODE = "NOT_FOUND";

    private final DecommissionedEndpointProperties properties;
    private final ObjectMapper objectMapper;

    public DecommissionedEndpointGuardFilter(DecommissionedEndpointProperties properties,
                                             ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isEnabled() || !properties.isBookingV2Crud()) {
            return true;
        }
        String path = resolvePath(request);
        return !BookingV2DecommissionedEndpointSupport.isDecommissionedBookingV2CrudRequest(
                path, request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = resolvePath(request);
        logger.info("Blocking decommissioned {} {}", request.getMethod(), path);

        HttpStatus status = HttpStatus.NOT_FOUND;
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ErrorMessage body = new ErrorMessage();
        body.setDetail(NOT_FOUND_DETAIL);
        body.setTitle(status.getReasonPhrase());
        body.setStatus(status.value());
        body.setInstance(path);
        body.setCode(NOT_FOUND_CODE);

        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private static String resolvePath(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.isEmpty()) {
            return BookingV2DecommissionedEndpointSupport.normalizePath(servletPath);
        }
        return BookingV2DecommissionedEndpointSupport.normalizePath(request.getRequestURI());
    }
}
