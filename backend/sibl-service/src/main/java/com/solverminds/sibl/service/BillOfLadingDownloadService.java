package com.solverminds.sibl.service;

import com.solverminds.bl.common.entity.Bl;
import com.solverminds.bl.common.repository.BlCustRepository;
import com.solverminds.bl.common.repository.BlRepository;
import com.solverminds.sibl.dto.v2.BlPrintResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Optional;

@Service
public class BillOfLadingDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(BillOfLadingDownloadService.class);

    private static final int CONNECTION_TIMEOUT = 30_000;
    private static final int READ_TIMEOUT = 60_000;

    private final BlRepository blRepository;
    private final BlCustRepository blCustRepository;

    @Value("${ecom.tracking.api.base-url}")
    private String trackingApiBaseUrl;

    private RestTemplate restTemplate;

    public BillOfLadingDownloadService(BlRepository blRepository,
                                       BlCustRepository blCustRepository) {
        this.blRepository = blRepository;
        this.blCustRepository = blCustRepository;
    }

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECTION_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Validate BL exists in the Documentation DB.
     * @return the Bl entity if found
     * @throws BillOfLadingNotFoundException if not found
     */
    public Bl validateBlExists(String blId) {
        Optional<Bl> bl = blRepository.findByBlNo(blId);
        if (!bl.isPresent()) {
            logger.warn("BL not found: {}", blId);
            throw new BillOfLadingNotFoundException("Bill of Lading '" + blId + "' was not found.");
        }
        return bl.get();
    }

    /**
     * Validate that the authenticated customer has access to this BL.
     * Checks bl_cust table for a matching custId with custtypeid AP or BP.
     * @throws CustomerAccessDeniedException if no matching customer record
     */
    public void validateCustomerAccess(String blId, String customerCode) {
        long count = blCustRepository.countByBlNoAndCustId(blId, customerCode);
        if (count == 0) {
            logger.warn("Customer {} does not have access to BL {}", customerCode, blId);
            throw new CustomerAccessDeniedException(
                "You do not have permission to access Bill of Lading '" + blId + "'.");
        }
    }

    /**
     * Fetch the BL print PDF from the external API.
     *
     * @param blNo     Bill of Lading number
     * @param userName login name extracted from JWT
     * @return PDF content as a byte array
     * @throws BlPrintServiceException if the API call fails or returns success=false
     */
    public byte[] fetchBlPrintPdf(String blNo, String userName) {
        String url = UriComponentsBuilder.fromHttpUrl(trackingApiBaseUrl)
                .path("/blprint")
                .queryParam("blNo", blNo)
                .queryParam("printType", "D")
                .queryParam("userName", userName)
                .toUriString();

        logger.info("Calling BL print API: blNo={}, userName={}", blNo, userName);

        try {
            ResponseEntity<BlPrintResponse> response = restTemplate.getForEntity(url, BlPrintResponse.class);
            BlPrintResponse body = response.getBody();

            if (body == null) {
                throw new BlPrintServiceException("BL print API returned an empty response.");
            }

            if (!Boolean.TRUE.equals(body.getSuccess())) {
                String apiMessage = (body.getMessage() != null && !body.getMessage().trim().isEmpty())
                        ? body.getMessage().trim()
                        : "BL print generation failed for '" + blNo + "'.";
                logger.warn("BL print API returned success=false for blNo={}: {}", blNo, apiMessage);
                throw new BlPrintServiceException(apiMessage);
            }

            if (body.getContent() == null || body.getContent().trim().isEmpty()) {
                throw new BlPrintServiceException("BL print API returned no PDF content for '" + blNo + "'.");
            }

            byte[] pdfBytes = Base64.getDecoder().decode(body.getContent());
            logger.info("BL print PDF retrieved successfully - blNo={}, size={} bytes", blNo, pdfBytes.length);
            return pdfBytes;

        } catch (BlPrintServiceException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            logger.error("Client error calling BL print API for blNo={}: {} {}", blNo, e.getStatusCode(), e.getMessage());
            throw new BlPrintServiceException("BL print service returned client error: " + e.getStatusCode());
        } catch (HttpServerErrorException e) {
            logger.error("Server error calling BL print API for blNo={}: {} {}", blNo, e.getStatusCode(), e.getMessage());
            throw new BlPrintServiceException("BL print service is temporarily unavailable. Please try again later.");
        } catch (ResourceAccessException e) {
            logger.error("Connection error calling BL print API for blNo={}: {}", blNo, e.getMessage());
            throw new BlPrintServiceException("Unable to connect to the BL print service. Please try again later.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode BL print PDF content for blNo={}: {}", blNo, e.getMessage());
            throw new BlPrintServiceException("BL print service returned invalid PDF content.");
        } catch (Exception e) {
            logger.error("Unexpected error calling BL print API for blNo={}: {}", blNo, e.getMessage(), e);
            throw new BlPrintServiceException("An unexpected error occurred while fetching the BL print PDF.");
        }
    }

    // --- Custom exceptions for clean error mapping in controller ---

    public static class BillOfLadingNotFoundException extends RuntimeException {
        public BillOfLadingNotFoundException(String message) {
            super(message);
        }
    }

    public static class CustomerAccessDeniedException extends RuntimeException {
        public CustomerAccessDeniedException(String message) {
            super(message);
        }
    }

    public static class BlPrintServiceException extends RuntimeException {
        public BlPrintServiceException(String message) {
            super(message);
        }
    }
}
