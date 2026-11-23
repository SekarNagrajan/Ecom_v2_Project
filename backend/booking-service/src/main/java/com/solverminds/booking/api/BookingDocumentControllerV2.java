package com.solverminds.booking.api;

import com.solverminds.booking.dto.v2.DocumentMetadata;
import com.solverminds.booking.dto.v2.ErrorMessage;
import com.solverminds.booking.security.CustomerContext;
import com.solverminds.booking.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * V2 Booking Documents API Controller.
 * Implements RESTful endpoints following the client's OpenAPI specification.
 * Completely isolated from V1 — existing V1 endpoints are NOT affected.
 */
@RestController
@RequestMapping("/api/v2/bookings")
@Tag(name = "Documents", description = "Lifecycle management for booking-related files.")
@SecurityRequirement(name = "bearerAuth")
public class BookingDocumentControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(BookingDocumentControllerV2.class);

    private final DocumentService documentService;
    private final CustomerContext customerContext;

    public BookingDocumentControllerV2(DocumentService documentService,
                                       CustomerContext customerContext) {
        this.documentService = documentService;
        this.customerContext = customerContext;
    }

    // ==================== GET /api/v2/bookings/{bookingId}/documents ====================

    @Operation(
        summary = "List booking documents",
        operationId = "listBookingDocuments",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "A list of document metadata.",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = DocumentMetadata.class)),
                    examples = {
                        @ExampleObject(
                            name = "DocumentsList",
                            summary = "Two documents attached to a booking",
                            value = "[\n" +
                                "  {\n" +
                                "    \"fileName\": \"customs_manifest.pdf\",\n" +
                                "    \"fileSize\": 102400,\n" +
                                "    \"lastModified\": \"2026-01-05T09:00:00Z\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"fileName\": \"invoice.pdf\",\n" +
                                "    \"fileSize\": 51200,\n" +
                                "    \"lastModified\": \"2026-01-04T17:00:00Z\"\n" +
                                "  }\n" +
                                "]"
                        ),
                        @ExampleObject(
                            name = "EmptyList",
                            summary = "No documents uploaded yet",
                            value = "[]"
                        )
                    }
                )
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failed (Missing or invalid Bearer token).",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.LIST_401))),
            @ApiResponse(responseCode = "403", description = "The client does not have access rights to the content.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.LIST_403))),
            @ApiResponse(responseCode = "404", description = "The resource could not be found.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.LIST_404))),
            @ApiResponse(responseCode = "500", description = "An unexpected server-side error occurred.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.LIST_500))),
            @ApiResponse(responseCode = "503", description = "The service is currently not available.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.LIST_503)))
        }
    )
    @GetMapping(
        path = "/{bookingId}/documents",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> listBookingDocuments(
            HttpServletRequest request,
            @Parameter(description = "Booking id, might be a booking request id (e.g. BKON123) or a confirmed booking id (e.g. TSHG1234)",
                       required = true)
            @PathVariable("bookingId") String bookingId) {
        try {
            String uri = request.getRequestURI();
            String token = extractAndValidateToken(request);
            String customerCode = customerContext.resolveCustomerCodeForBookingDetailRead(token);

            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("Booking ID is required", uri);
            }

            logger.info("V2 List documents - Booking: {}, Customer: {}", bookingId, customerCode);

            List<com.solverminds.booking.dto.DocumentListResponse> v1List =
                documentService.listDocumentsV2(bookingId.trim(), customerCode, false);

            List<DocumentMetadata> result = new ArrayList<>();
            for (com.solverminds.booking.dto.DocumentListResponse item : v1List) {
                DocumentMetadata meta = new DocumentMetadata(
                    item.getFileName(),
                    item.getSizeBytes(),
                    item.getLastModified() != null
                        ? item.getLastModified().atOffset(ZoneOffset.UTC)
                        : null
                );
                result.add(meta);
            }

            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage(), request.getRequestURI());
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed", request.getRequestURI());
        } catch (IllegalStateException e) {
            return serviceUnavailable(e.getMessage(), request.getRequestURI());
        } catch (Exception e) {
            logger.error("Unexpected error during V2 document listing for booking: {}", bookingId, e);
            return internalServerError("An unexpected error occurred while listing documents", request.getRequestURI());
        }
    }

    // ==================== POST /api/v2/bookings/{bookingId}/documents ====================

    @Operation(
        summary = "Upload booking documents",
        operationId = "uploadBookingDocuments",
        responses = {
            @ApiResponse(responseCode = "201", description = "Successfully uploaded."),
            @ApiResponse(responseCode = "400", description = "The server could not understand the request due to invalid syntax or parameters.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.UPLOAD_400))),
            @ApiResponse(responseCode = "401", description = "Authentication failed (Missing or invalid Bearer token).",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.UPLOAD_401))),
            @ApiResponse(responseCode = "403", description = "The client does not have access rights to the content.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.UPLOAD_403))),
            @ApiResponse(responseCode = "409", description = "Conflict with the current system state.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.UPLOAD_409))),
            @ApiResponse(responseCode = "500", description = "An unexpected server-side error occurred.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.UPLOAD_500))),
            @ApiResponse(responseCode = "503", description = "The service is currently not available.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.UPLOAD_503)))
        }
    )
    @PostMapping(
        path = "/{bookingId}/documents",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadBookingDocuments(
            HttpServletRequest request,
            @Parameter(description = "Booking id, might be a booking request id (e.g. BKON123) or a confirmed booking id (e.g. TSHG1234)",
                       required = true)
            @PathVariable("bookingId") String bookingId,
            @Parameter(description = "Array of binary files to upload", required = true)
            @RequestParam("files") List<MultipartFile> files) {
        try {
            String uri = request.getRequestURI();
            String token = extractAndValidateToken(request);
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", uri);
            }

            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("Booking ID is required", uri);
            }

            logger.info("V2 Upload documents - Booking: {}, Files: {}, Customer: {}",
                bookingId, files.size(), customerCode);

            documentService.uploadDocumentsV2(bookingId.trim(), files);

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage(), request.getRequestURI());
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                return conflict(e.getMessage(), request.getRequestURI());
            }
            if (e.getMessage() != null && e.getMessage().contains("setting not found")) {
                return serviceUnavailable(e.getMessage(), request.getRequestURI());
            }
            return internalServerError(e.getMessage(), request.getRequestURI());
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed", request.getRequestURI());
        } catch (Exception e) {
            logger.error("Unexpected error during V2 document upload for booking: {}", bookingId, e);
            return internalServerError("An unexpected error occurred while uploading documents", request.getRequestURI());
        }
    }

    // ==================== GET /api/v2/bookings/{bookingId}/documents/{fileName} ====================

    @Operation(
        summary = "Download document",
        operationId = "downloadBookingDocument",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The binary file content.",
                content = @Content(mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary"))
            ),
            @ApiResponse(responseCode = "401", description = "Authentication failed (Missing or invalid Bearer token).",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DOWNLOAD_401))),
            @ApiResponse(responseCode = "403", description = "The client does not have access rights to the content.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DOWNLOAD_403))),
            @ApiResponse(responseCode = "404", description = "The resource could not be found.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DOWNLOAD_404))),
            @ApiResponse(responseCode = "500", description = "An unexpected server-side error occurred.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DOWNLOAD_500))),
            @ApiResponse(responseCode = "503", description = "The service is currently not available.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DOWNLOAD_503)))
        }
    )
    @GetMapping(path = "/{bookingId}/documents/{fileName}")
    public ResponseEntity<?> downloadBookingDocument(
            HttpServletRequest request,
            @Parameter(description = "Booking id, might be a booking request id (e.g. BKON123) or a confirmed booking id (e.g. TSHG1234)",
                       required = true)
            @PathVariable("bookingId") String bookingId,
            @Parameter(description = "Name of a file", required = true)
            @PathVariable("fileName") String fileName) {
        try {
            String uri = request.getRequestURI();
            String token = extractAndValidateToken(request);
            String customerCode = customerContext.resolveCustomerCodeForBookingDetailRead(token);

            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("Booking ID is required", uri);
            }

            logger.info("V2 Download document - Booking: {}, File: {}, Customer: {}",
                bookingId, fileName, customerCode);

            File file = documentService.downloadDocumentV2(bookingId.trim(), fileName, customerCode);
            Resource resource = new FileSystemResource(file);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(file.length());
            headers.setContentDispositionFormData("attachment", file.getName());

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage(), request.getRequestURI());
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed", request.getRequestURI());
        } catch (IllegalStateException e) {
            return serviceUnavailable(e.getMessage(), request.getRequestURI());
        } catch (Exception e) {
            logger.error("Unexpected error during V2 document download - Booking: {}, File: {}",
                bookingId, fileName, e);
            return internalServerError("An unexpected error occurred while downloading the document", request.getRequestURI());
        }
    }

    // ==================== DELETE /api/v2/bookings/{bookingId}/documents/{fileName} ====================

    @Operation(
        summary = "Delete document",
        operationId = "deleteBookingDocument",
        description = "Permanently removes the document from the booking. This action cannot be undone.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted."),
            @ApiResponse(responseCode = "401", description = "Authentication failed (Missing or invalid Bearer token).",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DELETE_401))),
            @ApiResponse(responseCode = "403", description = "The client does not have access rights to the content.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DELETE_403))),
            @ApiResponse(responseCode = "404", description = "The resource could not be found.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DELETE_404))),
            @ApiResponse(responseCode = "409", description = "Conflict with the current system state.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DELETE_409))),
            @ApiResponse(responseCode = "500", description = "An unexpected server-side error occurred.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DELETE_500))),
            @ApiResponse(responseCode = "503", description = "The service is currently not available.",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = DocumentApiExamples.DELETE_503)))
        }
    )
    @DeleteMapping(path = "/{bookingId}/documents/{fileName}")
    public ResponseEntity<?> deleteBookingDocument(
            HttpServletRequest request,
            @Parameter(description = "Booking id, might be a booking request id (e.g. BKON123) or a confirmed booking id (e.g. TSHG1234)",
                       required = true)
            @PathVariable("bookingId") String bookingId,
            @Parameter(description = "Name of a file", required = true)
            @PathVariable("fileName") String fileName) {
        try {
            String uri = request.getRequestURI();
            String token = extractAndValidateToken(request);
            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", uri);
            }

            if (bookingId == null || bookingId.trim().isEmpty()) {
                return badRequest("Booking ID is required", uri);
            }

            logger.info("V2 Delete document - Booking: {}, File: {}, Customer: {}",
                bookingId, fileName, customerCode);

            documentService.deleteDocument(bookingId.trim(), fileName);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return notFound(e.getMessage(), request.getRequestURI());
        } catch (IllegalStateException e) {
            if (e.getMessage() != null && e.getMessage().contains("cannot be deleted")) {
                return conflict(e.getMessage(), request.getRequestURI());
            }
            return serviceUnavailable(e.getMessage(), request.getRequestURI());
        } catch (SecurityException e) {
            return unauthorized("Invalid JWT token or authentication failed", request.getRequestURI());
        } catch (Exception e) {
            logger.error("Unexpected error during V2 document delete - Booking: {}, File: {}",
                bookingId, fileName, e);
            return internalServerError("An unexpected error occurred while deleting the document", request.getRequestURI());
        }
    }

    // ==================== Helper Methods ====================

    private String extractAndValidateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new SecurityException("Missing or invalid Bearer token");
    }

    // ==================== RFC 7807 Error Response Methods ====================

    private ResponseEntity<ErrorMessage> badRequest(String detail, String instance) {
        ErrorMessage error = ErrorMessage.badRequest(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON).body(error);
    }

    private ResponseEntity<ErrorMessage> unauthorized(String detail, String instance) {
        ErrorMessage error = ErrorMessage.unauthorized(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON).body(error);
    }

    private ResponseEntity<ErrorMessage> notFound(String detail, String instance) {
        ErrorMessage error = ErrorMessage.notFound(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON).body(error);
    }

    private ResponseEntity<ErrorMessage> conflict(String detail, String instance) {
        ErrorMessage error = ErrorMessage.conflict(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON).body(error);
    }

    private ResponseEntity<ErrorMessage> internalServerError(String detail, String instance) {
        ErrorMessage error = ErrorMessage.internalServerError(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON).body(error);
    }

    private ResponseEntity<ErrorMessage> serviceUnavailable(String detail, String instance) {
        ErrorMessage error = ErrorMessage.serviceUnavailable(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON).body(error);
    }
}
