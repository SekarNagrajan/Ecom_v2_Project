package com.svm.ecom.api.registration.mapper;

import com.svm.ecom.api.registration.dto.RegistrationRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class RegistrationMapper {

    private RegistrationMapper() {}

    public static RegistrationRequest enrich(RegistrationRequest in, String nextRegNo) {
        if (isEmpty(in.getUserEmail())) {
            in.setUserEmail(in.getLoginName());
        }
        in.setRegisteredDate(OffsetDateTime.now().toString());
        if (isEmpty(in.getRandomNumber())) {
            // keep as string to avoid precision loss compared to sample
            in.setRandomNumber(UUID.randomUUID().toString().replace("-", ""));
        }
        if (isEmpty(in.getRegNo())) {
            in.setRegNo(nextRegNo);
        }
        // custCode is optional/null here unless you generate it
        return in;
    }
    
    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
