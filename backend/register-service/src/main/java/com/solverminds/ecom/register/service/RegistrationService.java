package com.solverminds.ecom.register.service;

import com.svm.ecom.api.registration.dto.RegistrationRequest;
import com.solverminds.ecom.register.dto.RegistrationResponse;

public interface RegistrationService {

    /**
     * Register new user
     * @param request Registration request details
     * @return RegistrationResponse containing success/failure information
     */
    RegistrationResponse register(RegistrationRequest request);
}
