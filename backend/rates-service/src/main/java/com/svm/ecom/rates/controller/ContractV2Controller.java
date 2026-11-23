package com.svm.ecom.rates.controller;

import com.svm.ecom.docs.SwaggerDocs;
import com.svm.ecom.dto.ErrorMessage;
import com.svm.ecom.dto.NestedErrorMessage;
import com.svm.ecom.rates.dto.CustomerContract;
import com.svm.ecom.rates.dto.CustomerContractListItem;
import com.svm.ecom.rates.security.CustomerContext;
import com.svm.ecom.rates.service.ContractDownloadService;
import com.svm.ecom.rates.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/contracts")
@Tag(name = SwaggerDocs.Contract.TAG_NAME, description = SwaggerDocs.Contract.TAG_DESCRIPTION)
public class ContractV2Controller {

    private static final Logger logger = LoggerFactory.getLogger(ContractV2Controller.class);

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractDownloadService contractDownloadService;

    @Autowired
    private CustomerContext customerContext;

    // GET /api/v2/contracts — Get all customer contracts
    @GetMapping
    @Operation(
        summary = SwaggerDocs.Contract.SUMMARY_GET_CONTRACTS,
        description = SwaggerDocs.Contract.DESC_GET_CONTRACTS,
        operationId = SwaggerDocs.Contract.OPERATION_ID_GET_CONTRACTS,
        responses = {
            @ApiResponse(responseCode = "200", description = "Response object containing a list of customer contracts.",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerContractListItem.class))))
        }
    )
    public ResponseEntity<?> getCustomerContracts(HttpServletRequest request) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                        "Unauthorized", 401, "/api/v2/contracts", "UNAUTHORIZED"));
            }

            String loginCustomerCode = customerContext.extractCustomerCodeFromToken(token);
            List<CustomerContractListItem> contracts = contractService.getCustomerContracts(loginCustomerCode);
            
            return ResponseEntity.ok(contracts);
        } catch (SecurityException e) {
            logger.warn("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                    "Unauthorized", 401, "/api/v2/contracts", "UNAUTHORIZED"));
        } catch (Exception e) {
            logger.error("Error fetching contracts: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("An unexpected server-side error occurred.",
                    "Internal Server Error", 500, "/api/v2/contracts", "INTERNAL_ERROR"));
        }
    }

    // GET /api/v2/contracts/{contractId} — Get customer contract by id
    @GetMapping("/{contractId}")
    @Operation(
        summary = SwaggerDocs.Contract.SUMMARY_GET_CONTRACT_BY_ID,
        description = SwaggerDocs.Contract.DESC_GET_CONTRACT_BY_ID,
        operationId = SwaggerDocs.Contract.OPERATION_ID_GET_CONTRACT_BY_ID,
        responses = {
            @ApiResponse(responseCode = "200", description = "Response object containing a single customer contract.",
                content = @Content(schema = @Schema(implementation = CustomerContract.class)))
        }
    )
    @Parameter(
        name = "contractId",
        description = SwaggerDocs.Contract.PARAM_CONTRACT_ID,
        required = true,
        example = SwaggerDocs.Contract.PARAM_CONTRACT_ID_EXAMPLE,
        schema = @Schema(type = "string")
    )
    @Parameter(
        name = "amendmentNumber",
        description = SwaggerDocs.Contract.PARAM_AMENDMENT_NUMBER,
        required = true,
        example = SwaggerDocs.Contract.PARAM_AMENDMENT_NUMBER_EXAMPLE,
        schema = @Schema(type = "integer")
    )
    public ResponseEntity<?> getCustomerContractById(
            @PathVariable String contractId,
            @RequestParam(value = "amendmentNumber") Integer amendmentNumber,
            HttpServletRequest request) {
        
        String instance = "/api/v2/contracts/" + contractId;
        try {
            if (contractId == null || contractId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildValidationError(instance, "contractId must not be empty.", "contractId", "INVALID_CONTRACT_ID"));
            }
            if (amendmentNumber == null || amendmentNumber < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildValidationError(instance, "amendmentNumber must be a valid positive integer.", "amendmentNumber", "INVALID_AMENDMENT_NUMBER"));
            }

            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                        "Unauthorized", 401, instance, "UNAUTHORIZED"));
            }

            String loginCustomerCode = customerContext.extractCustomerCodeFromToken(token);

            CustomerContract contract = contractService.getCustomerContractByIdAndAmendment(contractId, amendmentNumber, loginCustomerCode);
            
            if (contract == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildError("The contract could not be found or you don't have access to it.",
                        "Not Found", 404, instance, "CONTRACT_NOT_FOUND"));
            }
            
            return ResponseEntity.ok(contract);
        } catch (SecurityException e) {
            logger.warn("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                    "Unauthorized", 401, instance, "UNAUTHORIZED"));
        } catch (Exception e) {
            logger.error("Error fetching contract {}: {}", contractId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError("An unexpected server-side error occurred.",
                    "Internal Server Error", 500, instance, "INTERNAL_ERROR"));
        }
    }

    // GET /api/v2/contracts/{contractId}/download — Download contract PDF
    @GetMapping(path = "/{contractId}/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(
        summary = SwaggerDocs.Contract.SUMMARY_DOWNLOAD_CONTRACT,
        description = SwaggerDocs.Contract.DESC_DOWNLOAD_CONTRACT,
        operationId = SwaggerDocs.Contract.OPERATION_ID_DOWNLOAD_CONTRACT,
        responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Contract.RESPONSE_200_DOWNLOAD_CONTRACT,
                content = @Content(mediaType = "application/octet-stream",
                    schema = @Schema(type = "string", format = "binary")))
        }
    )
    @Parameter(
        name = "contractId",
        description = SwaggerDocs.Contract.PARAM_CONTRACT_ID,
        required = true,
        example = SwaggerDocs.Contract.PARAM_CONTRACT_ID_EXAMPLE,
        schema = @Schema(type = "string")
    )
    @Parameter(
        name = "amendmentNumber",
        description = SwaggerDocs.Contract.PARAM_AMENDMENT_NUMBER,
        required = true,
        example = SwaggerDocs.Contract.PARAM_AMENDMENT_NUMBER_EXAMPLE,
        schema = @Schema(type = "integer")
    )
    public ResponseEntity<?> downloadContract(
            @PathVariable String contractId,
            @RequestParam(value = "amendmentNumber") Integer amendmentNumber,
            HttpServletRequest request) {

        String instance = "/api/v2/contracts/" + contractId + "/download";
        try {
            if (contractId == null || contractId.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildValidationError(instance, "contractId must not be empty.", "contractId", "INVALID_CONTRACT_ID"));
            }
            if (amendmentNumber == null || amendmentNumber < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildValidationError(instance, "amendmentNumber must be a valid positive integer.", "amendmentNumber", "INVALID_AMENDMENT_NUMBER"));
            }

            String authorizationHeader = request.getHeader("Authorization");
            String token = extractJwtToken(authorizationHeader);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                        "Unauthorized", 401, instance, "UNAUTHORIZED"));
            }

            String loginCustomerCode = customerContext.extractCustomerCodeFromToken(token);

            String trimmedContractId = contractId.trim();
            logger.info("Contract download request - contractId: {}, amendmentNumber: {}, customer: {}",
                trimmedContractId, amendmentNumber, loginCustomerCode);

            Map<String, Object> contractDetails = contractDownloadService
                    .validateAndGetContractDetails(trimmedContractId, amendmentNumber, loginCustomerCode);

            byte[] pdfBytes = contractDownloadService.fetchContractPdf(contractDetails, loginCustomerCode);

            Resource resource = new ByteArrayResource(pdfBytes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(pdfBytes.length);
            headers.setContentDispositionFormData("attachment", trimmedContractId + ".pdf");

            logger.info("Contract download completed - contractId: {}, size: {} bytes", trimmedContractId, pdfBytes.length);
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (ContractDownloadService.ContractNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildError(e.getMessage(), "Not Found", 404, instance, "CONTRACT_NOT_FOUND"));
        } catch (ContractDownloadService.JasperServiceException e) {
            logger.error("Jasper service error for contract {}: {}", contractId, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildError(e.getMessage(), "Service Unavailable", 503, instance, "SERVICE_UNAVAILABLE"));
        } catch (SecurityException e) {
            logger.warn("Authentication failed for contract download: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildError("Authentication failed (Missing or invalid Bearer token).",
                    "Unauthorized", 401, instance, "UNAUTHORIZED"));
        } catch (Exception e) {
            logger.error("Error downloading contract {}: {}", contractId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildError("An unexpected server-side error occurred.",
                    "Internal Server Error", 500, instance, "INTERNAL_ERROR"));
        }
    }

    private String extractJwtToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private ErrorMessage buildError(String detail, String title, int status, String instance, String code) {
        ErrorMessage error = new ErrorMessage(detail, title, status, null, instance, code);
        NestedErrorMessage nested = new NestedErrorMessage();
        nested.setDetail(detail);
        nested.setCode(code);
        error.setErrors(Collections.singletonList(nested));
        return error;
    }

    private ErrorMessage buildValidationError(String instance, String paramDetail, String paramName, String paramCode) {
        ErrorMessage error = new ErrorMessage(
            "Invalid request parameters. Please review the provided contractId and amendmentNumber.",
            "Bad Request", 400, null, instance, "VALIDATION_FAILED");
        NestedErrorMessage nested = new NestedErrorMessage();
        nested.setDetail(paramDetail);
        nested.setParameter(paramName);
        nested.setCode(paramCode);
        error.setErrors(Collections.singletonList(nested));
        return error;
    }
}
