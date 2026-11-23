package com.svm.ecom.controller;

import com.svm.ecom.dto.AgencyLocationListResponse;
import com.svm.ecom.dto.ErrorResponse;
import com.svm.ecom.service.AgencyLocationService;
import com.svm.ecom.exception.ReportDbAccessException;
import com.svm.ecom.error.ErrorCodes;
import com.svm.ecom.error.ErrorReason;

// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// Spring imports
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/register")
@Tag(name = "Registration Services", description = "Registration-related operations including agency location lookup")
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private final AgencyLocationService agencyLocationService;

    public RegisterController(AgencyLocationService agencyLocationService) {
        this.agencyLocationService = agencyLocationService;
    }

    @GetMapping("/agency-locations")
    @Operation(
        summary = "List all agency locations (ports with agency codes)",
        description = "Returns all distinct ports and agency codes. Tries agency-with-ports query first, then falls back to agency-location query if no rows. Data is fetched from reportdb (read-only)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = SwaggerDocs.Common.SUCCESS_200,
            content = @Content(
                schema = @Schema(implementation = AgencyLocationListResponse.class),
                examples = @ExampleObject(
                    name = "SuccessResponse",
                    value = "{\"data\": [{\"portCode\": \"INNSA\", \"portName\": \"Nhava Sheva\", \"agencyCode\": \"INDMUM\"}, {\"portCode\": \"AEJEA\", \"portName\": \"Jebel Ali\", \"agencyCode\": \"AEJEA1\"}]}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = SwaggerDocs.Common.SERVER_ERROR_500,
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(
                    name = "DatabaseError",
                    value = "{\"statusCode\": 500, \"statusCodeText\": \"Internal Server Error\", \"errorDateTime\": \"2025-09-15T12:45:12\", \"errors\": [{\"errorCode\": 1000, \"reason\": \"apiError\", \"message\": \"Failed to fetch agency locations: Could not connect to reportdb\"}]}"
                )
            )
        )
    })
    public ResponseEntity<?> listAgencyLocations() {
        try {
            logger.info("Requesting all agency locations");
            
            AgencyLocationListResponse response = agencyLocationService.list();
            
            logger.info("Successfully returned {} locations", response.getData().size());
            
            return ResponseEntity.ok(response);
            
        } catch (ReportDbAccessException e) {
            logger.error("Database access error while fetching agency locations", e);
            
            // Create standardized error response for API error (1000-1999 range)
            ErrorResponse errorResponse = new ErrorResponse(500, "Internal Server Error", LocalDateTime.now());
            errorResponse.addError(ErrorCodes.INTERNAL_ERROR, null, null, ErrorReason.INTERNAL_ERROR, 
                "Failed to fetch agency locations: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
            
        } catch (Exception e) {
            logger.error("Unexpected error while fetching agency locations", e);
            
            // Create standardized error response for general internal error
            ErrorResponse errorResponse = new ErrorResponse(500, "Internal Server Error", LocalDateTime.now());
            errorResponse.addError(ErrorCodes.INTERNAL_ERROR, null, null, ErrorReason.INTERNAL_ERROR, 
                "An unexpected error occurred while processing the request");
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
