package com.solverminds.ecom.register.service.impl;

import com.solverminds.ecom.register.constants.RegistrationMessages;
import com.svm.ecom.api.registration.dto.RegistrationRequest;
import com.solverminds.ecom.register.dto.RegistrationResponse;
import com.solverminds.ecom.register.dto.CompanyCity;
import com.solverminds.ecom.register.entity.EcomRegistration;
import com.solverminds.ecom.register.repo.EcomRegistrationRepository;
import com.solverminds.ecom.register.repo.CustomerMasterRepository;
import com.solverminds.ecom.register.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.time.LocalDateTime;

@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class RegistrationServiceImpl implements RegistrationService {

    private final EcomRegistrationRepository registrationRepository;
    private final CustomerMasterRepository customerMasterRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RegistrationServiceImpl(EcomRegistrationRepository registrationRepository,
                                 CustomerMasterRepository customerMasterRepository,
                                 JdbcTemplate jdbcTemplate) {
        this.registrationRepository = registrationRepository;
        this.customerMasterRepository = customerMasterRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        try {
            String login = request.getLoginName();
            
            // Step 1: Check for duplicate login name (case-insensitive) in ecom_reg_details
            if (registrationRepository.existsByLoginNameIgnoreCase(login)) {
                return RegistrationResponse.of(true, RegistrationMessages.LOGIN_NAME_ALREADY_EXISTS, null, login);
            }
            
            // Step 2: Validate customer in ecomm_jsf.customermaster
            Optional<CompanyCity> ccOpt = customerMasterRepository.findActiveByCustCode(request.getCustCode());
            if (!ccOpt.isPresent()) {
                return RegistrationResponse.of(true, "Invalid Customer Code", null, login);
            }
            
            // Step 3: Optional company name check if compName is present in request
            if (request.getCompName() != null && 
                !request.getCompName().equalsIgnoreCase(ccOpt.get().getCompanyName())) {
                return RegistrationResponse.of(true, "Company name mismatch for customer code", null, login);
            }
            
            // Step 4: All validations passed - proceed with registration
            // Use provided regNo or generate atomic registration number if not present
            String regNo = (request.getRegNo() != null && !request.getRegNo().trim().isEmpty()) 
                          ? request.getRegNo() 
                          : nextRegNo("REG");

            // Insert into registration table with all required fields and defaults
            insertRegistrationRecord(request, regNo);

            // Return success response
            return RegistrationResponse.of(true, "Registration completed successfully", regNo, login);
            
        } catch (Exception e) {
            // Log the error but still return consistent envelope with HTTP 200
            String login = request.getLoginName() != null ? request.getLoginName() : "";
            return RegistrationResponse.of(true, "Registration failed: " + e.getMessage(), null, login);
        }
    }

    /**
     * Generate atomic registration number using SQL Server UPDATE with OUTPUT
     * This ensures no duplicate registration numbers under concurrent requests
     */
    @Transactional
    private String nextRegNo(String moduleId) {
        return jdbcTemplate.query(
            "UPDATE ecomMdlIndex WITH (ROWLOCK, XLOCK) " +
            "SET mdlindex = mdlindex + 1 " +
            "OUTPUT inserted.mdlprefix, inserted.mdlindex " +
            "WHERE mdlid = ?",
            ps -> ps.setString(1, moduleId),
            rs -> {
                if (rs.next()) {
                    String prefix = rs.getString(1);
                    int idx = rs.getInt(2);
                    return prefix + idx;
                }
                throw new IllegalStateException("No mdlid row found for: " + moduleId);
            }
        );
    }

    /**
     * Generate random unique number for registration
     */
    private String generateRandomId() {
        String randomid1 = "", randomid2 = "", randomid3 = "", randomid = "";
        Random generator = null;
        try {
            generator = new Random();
            randomid1 = String.valueOf(generator.nextInt(10000) * generator.nextInt(10000));
            randomid2 = String.valueOf(generator.nextInt(10000) * generator.nextInt(10000));
            randomid3 = String.valueOf(generator.nextInt(10000) * generator.nextInt(10000));
            randomid = randomid1 + randomid2 + randomid3;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return randomid;
    }

    /**
     * Insert registration record into ecom_reg_details table
     * using JPA save method with entity defaults
     */
    private void insertRegistrationRecord(RegistrationRequest dto, String regNo) {
        EcomRegistration registration = new EcomRegistration();
        
        // Set required fields from DTO
        registration.setRegNo(regNo);
        registration.setCustCode(dto.getCustCode());
        registration.setCompName(dto.getCompName());
        registration.setAddress(dto.getAddress());
        registration.setCity(dto.getCity());
        registration.setControlLocation(dto.getControlLocation());
        registration.setCountry(dto.getCountry());
        registration.setPinCode(dto.getPinCode());
        registration.setCompPhone(trimToNull(dto.getCompPhone()));
        registration.setCompDomain(dto.getCompDomain());
        registration.setLoginName(dto.getLoginName());
        registration.setPassword(dto.getPassword());
        registration.setFirstName(dto.getFirstName());
        registration.setLastName(dto.getLastName());
        registration.setUserPhone(trimToNull(dto.getUserPhone()));
        registration.setUserFax(dto.getUserFax());
        registration.setUserEmail(dto.getUserEmail() != null ? dto.getUserEmail() : dto.getLoginName()); // Default userEmail to loginName
        registration.setPrefLang(dto.getPrefLang());
        registration.setUserMobile(dto.getUserMobile());
        registration.setTitle(dto.getTitle());
        registration.setAgencyCode(dto.getAgencyCode());
        registration.setRandomNumber(generateRandomId());
        registration.setRegisteredDate(LocalDateTime.now());
        
        // Default values will be set by @PrePersist in the entity
        registrationRepository.save(registration);
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
