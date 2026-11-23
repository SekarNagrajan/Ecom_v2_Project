package com.solverminds.sibl.api;

import com.solverminds.sibl.dto.v2.ErrorMessage;
import com.solverminds.sibl.security.CustomerContext;
import com.solverminds.sibl.service.BillOfLadingDownloadService;
import com.solverminds.sibl.service.BillOfLadingDownloadService.BillOfLadingNotFoundException;
import com.solverminds.sibl.service.BillOfLadingDownloadService.BlPrintServiceException;
import com.solverminds.sibl.service.BillOfLadingDownloadService.CustomerAccessDeniedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2/billOfLading")
@Tag(name = "Bill of Lading", description = "Operations related to Bill of Lading documents.")
@SecurityRequirement(name = "bearerAuth")
public class BillOfLadingV2Controller {

    private static final Logger logger = LoggerFactory.getLogger(BillOfLadingV2Controller.class);

    private final BillOfLadingDownloadService downloadService;
    private final CustomerContext customerContext;

    public BillOfLadingV2Controller(BillOfLadingDownloadService downloadService,
                                    CustomerContext customerContext) {
        this.downloadService = downloadService;
        this.customerContext = customerContext;
    }

    @Operation(
        summary = "Download Bill of Lading document with the specified id",
        operationId = "downloadBillOfLading",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The binary file content.",
                content = @Content(
                    mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary")
                )
            ),
            @ApiResponse(responseCode = "401",
                description = "Authentication failed (Missing or invalid Bearer token).",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = BillOfLadingApiExamples.DOWNLOAD_401))),
            @ApiResponse(responseCode = "403",
                description = "The client does not have access rights to the content.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = BillOfLadingApiExamples.DOWNLOAD_403))),
            @ApiResponse(responseCode = "404",
                description = "The resource could not be found.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = BillOfLadingApiExamples.DOWNLOAD_404))),
            @ApiResponse(responseCode = "500",
                description = "An unexpected server-side error occurred.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = BillOfLadingApiExamples.DOWNLOAD_500))),
            @ApiResponse(responseCode = "503",
                description = "The service is currently not available.",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorMessage.class),
                    examples = @ExampleObject(value = BillOfLadingApiExamples.DOWNLOAD_503)))
        }
    )
    @GetMapping(path = "/{blId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadBillOfLading(
            HttpServletRequest request,
            @Parameter(description = "Bill of Lading ID", required = true, example = "TSHG2500001")
            @PathVariable("blId") String blId) {

        String instance = "/api/v2/billOfLading/" + blId + "/download";

        try {
            String token = extractAndValidateToken(request);

            String customerCode = customerContext.extractCustomerCodeFromToken(token);
            if (customerCode == null || customerCode.trim().isEmpty()) {
                return unauthorized("Customer code missing in JWT", instance);
            }

            String userName = customerContext.extractLoginFromToken(token);

            if (blId == null || blId.trim().isEmpty()) {
                return badRequest("Bill of Lading ID is required", instance);
            }

            String trimmedBlId = blId.trim();
            logger.info("BL download request - blId: {}, customer: {}, user: {}", trimmedBlId, customerCode, userName);

            downloadService.validateBlExists(trimmedBlId);
            downloadService.validateCustomerAccess(trimmedBlId, customerCode);

            byte[] pdfBytes = downloadService.fetchBlPrintPdf(trimmedBlId, userName);

            Resource resource = new ByteArrayResource(pdfBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", trimmedBlId + ".pdf");

            logger.info("BL download completed - blId: {}, size: {} bytes", trimmedBlId, pdfBytes.length);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (BillOfLadingNotFoundException e) {
            return notFound(e.getMessage(), instance);
        } catch (CustomerAccessDeniedException e) {
            return forbidden(e.getMessage(), instance);
        } catch (BlPrintServiceException e) {
            logger.error("BL print service error for blId={}: {}", blId, e.getMessage());
            return serviceUnavailable(e.getMessage(), instance);
        } catch (SecurityException e) {
            logger.warn("Authentication failed for BL download: {}", e.getMessage());
            return unauthorized("Authentication failed (Missing or invalid Bearer token).", instance);
        } catch (Exception e) {
            logger.error("Unexpected error during BL download - blId: {}", blId, e);
            return internalServerError(
                "An unexpected error occurred while downloading the Bill of Lading.", instance);
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

    private ResponseEntity<ErrorMessage> badRequest(String detail, String instance) {
        ErrorMessage error = new ErrorMessage(detail, "Bad Request", 400);
        error.setCode("BAD_REQUEST");
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

    private ResponseEntity<ErrorMessage> forbidden(String detail, String instance) {
        ErrorMessage error = ErrorMessage.forbidden(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON).body(error);
    }

    private ResponseEntity<ErrorMessage> notFound(String detail, String instance) {
        ErrorMessage error = ErrorMessage.notFound(detail);
        error.setInstance(instance);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
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
