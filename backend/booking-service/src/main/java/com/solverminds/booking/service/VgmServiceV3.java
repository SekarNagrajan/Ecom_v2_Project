package com.solverminds.booking.service;

import com.solverminds.booking.dto.v3.vgm.VgmInfoV3;
import com.solverminds.booking.repo.VgmRepositoryV3;
import org.springframework.stereotype.Service;

@Service
public class VgmServiceV3 {

    private final VgmRepositoryV3 vgmRepositoryV3;

    public VgmServiceV3(VgmRepositoryV3 vgmRepositoryV3) {
        this.vgmRepositoryV3 = vgmRepositoryV3;
    }

    public void saveVGMDetails(String bookingId, VgmInfoV3 request, String customerCode, String loginUser) {
        vgmRepositoryV3.saveVGMDetails(bookingId, request, customerCode, loginUser);
    }

    public VgmInfoV3 getVGMDetails(String bookingId, String customerCode) {
        return vgmRepositoryV3.getVGMDetails(bookingId, customerCode);
    }
}
