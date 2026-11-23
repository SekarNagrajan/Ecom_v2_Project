package com.svm.ecom.rates.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.rates.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

@Service
public class ContractDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(ContractDownloadService.class);

    private static final int CONNECTION_TIMEOUT = 30_000;
    private static final int READ_TIMEOUT = 60_000;
    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm";

    private final ContractRepository contractRepository;
    private final ObjectMapper objectMapper;
    private RestTemplate restTemplate;

    public ContractDownloadService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECTION_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Validate that the contract exists and the customer has access.
     * Returns the full contract row (with carrierCode, shiptype, etc.) for PDF generation.
     *
     * @throws ContractNotFoundException if not found or not accessible
     */
    public Map<String, Object> validateAndGetContractDetails(String contractId, Integer amendmentNumber, String customerCode) {
        Map<String, Object> row = contractRepository.findContractForDownload(contractId, amendmentNumber, customerCode);
        if (row == null) {
            throw new ContractNotFoundException(
                "Contract '" + contractId + "' with amendment " + amendmentNumber
                + " was not found or you don't have access to it.");
        }
        return row;
    }

    /**
     * Generate and fetch the contract PDF from the Jasper report service.
     *
     * @param contractDetails row from findContractForDownload (contains carrierCode, shiptype, effectivedate, etc.)
     * @param customerCode    authenticated customer code (used as Username in Jasper input)
     * @return PDF content as byte array
     * @throws JasperServiceException if the API call fails
     */
    public byte[] fetchContractPdf(Map<String, Object> contractDetails, String customerCode) {
        String contractNo = (String) contractDetails.get("contractNo");
        Integer amendmentNo = parseInteger(contractDetails.get("amendmentNo"), 0);

        String baseUrl = contractRepository.getJasperBaseUrl();
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new JasperServiceException("Jasper report base URL is not configured (globalconfigproduct.blprintwsurl).");
        }

        String reportPath = contractRepository.getReportPath();
        if (reportPath == null || reportPath.trim().isEmpty()) {
            throw new JasperServiceException("Report path is not configured (rptserverinfo).");
        }

        String dateFormat = contractRepository.getDateFormat(customerCode);
        if (dateFormat == null || dateFormat.trim().isEmpty()) {
            dateFormat = DEFAULT_DATE_FORMAT;
        }

        String grpType = contractRepository.getGrpType(contractNo, amendmentNo);
        String jasperId = resolveJasperId(grpType);

        Object effectiveDateObj = contractDetails.get("effectivedate");
        String effectiveDateStr = effectiveDateObj != null ? String.valueOf(effectiveDateObj) : "";
        String shipType = resolveShipType((String) contractDetails.get("shiptype"));
        String carrierCode = (String) contractDetails.get("carrierCode");
        if (carrierCode == null) carrierCode = "";

        String contractName = (String) contractDetails.get("ContractName");

        String userup = (String) contractDetails.get("userup");
        String usrcr = (String) contractDetails.get("usrcr");
        String username = (userup != null && !userup.trim().isEmpty()) ? userup.trim() :
                          (usrcr != null && !usrcr.trim().isEmpty()) ? usrcr.trim() : customerCode;

        Map<String, Object> requestBody = buildJasperRequest(
                contractNo, amendmentNo, dateFormat, effectiveDateStr,
                shipType, carrierCode, username, reportPath, jasperId, contractName);

        String url = baseUrl.endsWith("/") ? baseUrl + "JasperWeb/generate/" : baseUrl + "/JasperWeb/generate/";
        logger.info("Calling Jasper API for contract PDF - contractNo: {}, amendment: {}, url: {}",
                contractNo, amendmentNo, url);

        return callJasperApi(url, requestBody, contractNo);
    }

    private Map<String, Object> buildJasperRequest(
            String contractNo, Integer amendmentNo, String dateFormat,
            String effectiveDateStr, String shipType, String carrierCode,
            String customerCode, String reportPath, String jasperId,
            String contractName) {

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("refno", Collections.singletonList(contractNo));
        input.put("contractno", contractNo);
        input.put("nfrfilename", buildNfrFilename(contractName, contractNo));
        input.put("amendmentNo", String.valueOf(amendmentNo));
        input.put("dateformat", dateFormat);
        input.put("amdeffdate", effectiveDateStr);
        input.put("amdeffdate1", effectiveDateStr);
        input.put("stype", shipType);
        input.put("shiptype", shipType);
        input.put("Jasperprint", "DB");
        input.put("carriercode", carrierCode.trim());
        input.put("logo", reportPath + File.separator + "images" + File.separator + "RRQCOM.gif");
        //input.put("logo", reportPath + "/images/RRQCOM.gif");
        input.put("Remarks", "Ecom-API");
        input.put("Username", customerCode);
        input.put("path", reportPath.endsWith(File.separator) ? reportPath : reportPath + File.separator);
        //input.put("path", reportPath.endsWith("/") ? reportPath : reportPath + "/");
        input.put("jasperid", jasperId);
        input.put("moduleid", "CUC");
        input.put("agencycode", "N/A");
        input.put("locationcode", "N/A");

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("datasource", "COM");
        details.put("title", "Contract Managment System");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("input", input);
        body.put("details", details);

        return body;
    }

    @SuppressWarnings("unchecked")
    private byte[] callJasperApi(String url, Map<String, Object> requestBody, String contractNo) {
        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            logger.debug("Jasper API request body: {}", jsonBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.ALL));

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            String responseBody = response.getBody();
            if (responseBody == null || responseBody.trim().isEmpty()) {
                throw new JasperServiceException("Jasper API returned an empty response.");
            }

            Map<String, Object> result = objectMapper.readValue(responseBody, Map.class);
            String status = result.get("status") != null ? String.valueOf(result.get("status")) : "";

            if (!"200".equals(status)) {
                String message = result.get("message") != null ? String.valueOf(result.get("message")) : "";
                logger.warn("Jasper API returned non-200 status for contract {}: status={}, message={}, fullResponse={}",
                        contractNo, status, message, responseBody);
                throw new JasperServiceException(
                        message.isEmpty() ? "Contract PDF generation failed for '" + contractNo + "'." : message);
            }

            String content = (String) result.get("content");
            if (content == null || content.trim().isEmpty()) {
                throw new JasperServiceException("Jasper API returned no PDF content for contract '" + contractNo + "'.");
            }

            byte[] pdfBytes = Base64.getDecoder().decode(content);
            logger.info("Contract PDF retrieved - contractNo: {}, size: {} bytes", contractNo, pdfBytes.length);
            return pdfBytes;

        } catch (JasperServiceException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            logger.error("Client error calling Jasper API for contract {}: {} {}",
                    contractNo, e.getStatusCode(), e.getMessage());
            throw new JasperServiceException("Jasper service returned client error: " + e.getStatusCode());
        } catch (HttpServerErrorException e) {
            logger.error("Server error calling Jasper API for contract {}: {} {}",
                    contractNo, e.getStatusCode(), e.getMessage());
            throw new JasperServiceException("Jasper service is temporarily unavailable. Please try again later.");
        } catch (ResourceAccessException e) {
            logger.error("Connection error calling Jasper API for contract {}: {}", contractNo, e.getMessage());
            throw new JasperServiceException("Unable to connect to the Jasper service. Please try again later.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode PDF content for contract {}: {}", contractNo, e.getMessage());
            throw new JasperServiceException("Jasper service returned invalid PDF content.");
        } catch (Exception e) {
            logger.error("Unexpected error calling Jasper API for contract {}: {}", contractNo, e.getMessage(), e);
            throw new JasperServiceException("An unexpected error occurred while generating the contract PDF.");
        }
    }

    /**
     * Resolve Jasper template ID based on grptype from rate detail.
     * US -> CUC2, IN -> CUC_IN, default -> CUC_EUR
     */
    private String resolveJasperId(String grpType) {
        if (grpType == null) return "CUC_EUR";
        switch (grpType.trim().toUpperCase()) {
            case "US": return "CUC2";
            case "IN": return "CUC_IN";
            default:   return "CUC_EUR";
        }
    }

    /**
     * Resolve shiptype: "BB" stays "BB", everything else becomes "CN".
     */
    private String resolveShipType(String shipType) {
        if (shipType != null && shipType.trim().equalsIgnoreCase("BB")) {
            return "BB";
        }
        return "CN";
    }

    /**
     * Build nfrfilename from contract name: sanitize special chars, append contractNo, remove whitespace.
     * If contractName is present, use it as-is (CMS mode); otherwise fall back to "Contract" prefix.
     */
    private String buildNfrFilename(String contractName, String contractNo) {
        String custname;
        if (contractName != null && !contractName.trim().isEmpty()) {
            custname = contractName.replace(",", " ").replace(".", " ").replace(";", " ");
        } else {
            custname = "";
        }
        return (custname + "_" + contractNo.trim()).replaceAll("\\s", "");
    }

    private static Integer parseInteger(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).intValue();
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // --- Custom exceptions ---

    public static class ContractNotFoundException extends RuntimeException {
        public ContractNotFoundException(String message) {
            super(message);
        }
    }

    public static class JasperServiceException extends RuntimeException {
        public JasperServiceException(String message) {
            super(message);
        }
    }
}
