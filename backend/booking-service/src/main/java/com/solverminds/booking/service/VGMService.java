package com.solverminds.booking.service;

import com.solverminds.booking.dto.v2.vgm.VGMInfo;
import com.solverminds.booking.repo.VgmRepository;
import org.springframework.stereotype.Service;

@Service
public class VGMService {

    private final VgmRepository vgmRepository;

    public VGMService(VgmRepository vgmRepository) {
        this.vgmRepository = vgmRepository;
    }

    /**
     * @param customerCode JWT customer code (trimmed); used for AP/BP access checks
     */
    /**
     * Upsert VGM for all listed containers (ECOM + LRP).
     *
     * @param loginUser JWT username / subject for ecom_vgmsolas usercr/userup
     */
    public void saveVGMDetails(String bookingId, VGMInfo request, String customerCode, String loginUser) {
        vgmRepository.saveVGMDetails(bookingId, request, customerCode, loginUser);
    }

    public VGMInfo getVGMDetails(String bookingId, String customerCode) {
        return vgmRepository.getVGMDetails(bookingId, customerCode);
    }
}
