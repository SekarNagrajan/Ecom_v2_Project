package com.solverminds.ecom.register.service;

import com.solverminds.ecom.register.dto.QuickRegistrationRequest;
import com.solverminds.ecom.register.dto.QuickRegistrationResponse;

public interface QuickRegistrationService {

    QuickRegistrationResponse quickRegister(QuickRegistrationRequest request);
}
