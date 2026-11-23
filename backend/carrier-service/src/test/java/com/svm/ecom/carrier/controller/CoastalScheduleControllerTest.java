package com.svm.ecom.carrier.controller;

import com.svm.ecom.carrier.model.CoastalScheduleBlock;
import com.svm.ecom.carrier.model.CoastalScheduleCall;
import com.svm.ecom.carrier.service.CoastalScheduleException;
import com.svm.ecom.carrier.service.CoastalScheduleService;
import com.svm.ecom.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CoastalScheduleControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CoastalScheduleService coastalScheduleService;

    @InjectMocks
    private CarrierController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void returnsIadScheduleForAValidCall() throws Exception {
        when(jwtUtil.validateTokenWithSession(anyString())).thenReturn("tester");
        CoastalScheduleCall call = new CoastalScheduleCall();
        call.setPortName("SINGAPORE");
        call.setStatus(com.svm.ecom.carrier.enums.CallStatus.S);
        when(coastalScheduleService.fetch(anyList(), anyString()))
                .thenReturn(Collections.singletonList(new CoastalScheduleBlock(Collections.singletonList(call))));

        mockMvc.perform(post("/api/v1/schedule/getscheduledetail")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"fromDate\":\"2026-06-01\",\"toDate\":\"2026-06-30\",\"service\":\"EGSC\",\"vessel\":\"EGSV\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coastalSchedule[0].portName").value("SINGAPORE"))
                .andExpect(jsonPath("$[0].coastalSchedule[0].status").value("S"));
    }

    @Test
    void missingBearerTokenIsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/schedule/getscheduledetail")
                        .header("Authorization", "Token abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"fromDate\":\"2026-06-01\",\"toDate\":\"2026-06-30\",\"service\":\"EGSC\",\"vessel\":\"EGSV\"}]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.errors[0].message").value("Missing or invalid Authorization header"));
    }

    @Test
    void invalidJwtIsUnauthorized() throws Exception {
        when(jwtUtil.validateTokenWithSession(anyString()))
                .thenThrow(new RuntimeException("JWT token has expired"));

        mockMvc.perform(post("/api/v1/schedule/getscheduledetail")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"fromDate\":\"2026-06-01\",\"toDate\":\"2026-06-30\",\"service\":\"EGSC\",\"vessel\":\"EGSV\"}]"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.errors[0].message").value("JWT token has expired"));
    }

    @Test
    void liveFailureUsesTheServiceStatus() throws Exception {
        when(jwtUtil.validateTokenWithSession(anyString())).thenReturn("tester");
        when(coastalScheduleService.fetch(anyList(), anyString()))
                .thenThrow(new CoastalScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Coastal schedule service is unavailable"));

        mockMvc.perform(post("/api/v1/schedule/getscheduledetail")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"fromDate\":\"2026-06-01\",\"toDate\":\"2026-06-30\",\"service\":\"EGSC\",\"vessel\":\"EGSV\"}]"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.statusCode").value(503))
                .andExpect(jsonPath("$.errors[0].message").value("Coastal schedule service is unavailable"));
    }
}
