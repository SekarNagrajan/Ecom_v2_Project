package com.solverminds.ecom.register.service;

import com.solverminds.ecom.register.dto.ProfileDto;

public interface ProfileService {

    /**
     * Get user profile by email
     */
    ProfileDto getProfile(String email);


}
