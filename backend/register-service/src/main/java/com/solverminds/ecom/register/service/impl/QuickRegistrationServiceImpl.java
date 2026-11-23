package com.solverminds.ecom.register.service.impl;

import com.solverminds.ecom.register.constants.RegistrationMessages;
import com.solverminds.ecom.register.dto.QuickRegistrationRequest;
import com.solverminds.ecom.register.dto.QuickRegistrationResponse;
import com.solverminds.ecom.register.entity.EcomUserDetails;
import com.solverminds.ecom.register.repo.EcomUserDetailsRepository;
import com.solverminds.ecom.register.service.QuickRegistrationService;
import com.solverminds.ecom.register.util.DummyCustomerCodeGenerator;
import com.svm.ecom.common.sequence.RegNoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@Transactional(transactionManager = "ecomTransactionManager")
public class QuickRegistrationServiceImpl implements QuickRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(QuickRegistrationServiceImpl.class);

    private static final String DUMMY_STATUS = "D";
    private static final String USER_ROLE = "Y";
    private static final String MENU_ID = "44_47_48_52";
    private static final String LOCKED_STATUS = "N";
    private static final String DEFAULT_TIMEZONE =
            "+05:30 (GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi";
    private static final int MAX_INSERT_ATTEMPTS = 10;
    private static final String MSG_SUCCESS = "Quick registration successful";
    private static final String MSG_DUPLICATE_EMAIL = RegistrationMessages.LOGIN_NAME_ALREADY_EXISTS;
    private static final String MSG_INVALID_EMAIL = "Invalid email format";
    private static final String MSG_RETRY_EXHAUSTED =
            "Quick registration failed. Please try again.";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private final EcomUserDetailsRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DummyCustomerCodeGenerator customerCodeGenerator;
    private final RegNoService regNoService;

    @Autowired
    public QuickRegistrationServiceImpl(EcomUserDetailsRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        DummyCustomerCodeGenerator customerCodeGenerator,
                                        RegNoService regNoService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.customerCodeGenerator = customerCodeGenerator;
        this.regNoService = regNoService;
    }

    @Override
    public QuickRegistrationResponse quickRegister(QuickRegistrationRequest request) {
        String rawEmail = request.getEmailId();
        if (!isValidEmail(rawEmail)) {
            return QuickRegistrationResponse.of(true, MSG_INVALID_EMAIL, null, rawEmail);
        }

        String emailId = rawEmail.trim();

        if (userRepository.existsByEmailIDIgnoreCase(emailId)) {
            return QuickRegistrationResponse.of(true, MSG_DUPLICATE_EMAIL, null, emailId);
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        String timezone = resolveTimezone(request.getPreferredTimezone());
        LocalDateTime now = LocalDateTime.now();

        for (int attempt = 1; attempt <= MAX_INSERT_ATTEMPTS; attempt++) {
            String customerCode = customerCodeGenerator.generate();
            EcomUserDetails user = buildProvisionalUser(emailId, hashedPassword, customerCode, timezone, now);
            try {
                userRepository.saveAndFlush(user);
                String regNo = regNoService.nextRegNo();
                logger.info("Quick registration completed for email={}, customerCode={}, regNo={}",
                        emailId, customerCode, regNo);
                return QuickRegistrationResponse.of(true, MSG_SUCCESS, regNo, emailId);
            } catch (DataIntegrityViolationException ex) {
                logger.debug("Customer code collision on attempt {} for email={}: {}",
                        attempt, emailId, customerCode);
            }
        }

        logger.warn("Quick registration failed after {} attempts for email={}", MAX_INSERT_ATTEMPTS, emailId);
        return QuickRegistrationResponse.of(true, MSG_RETRY_EXHAUSTED, null, emailId);
    }

    private static boolean isValidEmail(String email) {
        return email != null
                && !email.trim().isEmpty()
                && email.length() <= 100
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    private EcomUserDetails buildProvisionalUser(String emailId,
                                                 String hashedPassword,
                                                 String customerCode,
                                                 String timezone,
                                                 LocalDateTime now) {
        EcomUserDetails user = new EcomUserDetails();
        user.setEmailID(emailId);
        user.setPassword(hashedPassword);
        user.setUserRole(USER_ROLE);
        user.setCustomerCode(customerCode);
        user.setStatus(DUMMY_STATUS);
        user.setAcceptedDate(now);
        user.setMenuId(MENU_ID);
        user.setCcmailid(emailId);
        user.setApptoken(null);
        user.setPreferredTimezone(timezone);
        user.setLastpwdup(now);
        user.setLogindate(null);
        user.setLogoutdate(null);
        user.setWrgPassAtmpt(0);
        user.setLockedStatus(LOCKED_STATUS);
        user.setLockedTime(null);
        return user;
    }

    private String resolveTimezone(String preferredTimezone) {
        if (preferredTimezone == null || preferredTimezone.trim().isEmpty()) {
            return DEFAULT_TIMEZONE;
        }
        return preferredTimezone.trim();
    }
}
