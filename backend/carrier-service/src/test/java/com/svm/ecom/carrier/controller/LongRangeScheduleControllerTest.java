package com.svm.ecom.carrier.controller;

import com.svm.ecom.carrier.model.LongRangeScheduleBlock;
import com.svm.ecom.carrier.model.LongRangeScheduleCall;
import com.svm.ecom.carrier.service.LongRangeScheduleException;
import com.svm.ecom.carrier.service.LongRangeScheduleService;
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
class LongRangeScheduleControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private LongRangeScheduleService longRangeScheduleService;

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
        LongRangeScheduleCall call = new LongRangeScheduleCall();
        call.setPortName("NHAVA SHEVA,MUMBAI");
        call.setMaxTeu(900);
        call.setCallStatus(com.svm.ecom.carrier.enums.CallStatus.S);
        call.setScheduleWeek(6);
        when(longRangeScheduleService.fetch(anyList(), anyString()))
                .thenReturn(Collections.singletonList(new LongRangeScheduleBlock(Collections.singletonList(call))));

        mockMvc.perform(post("/api/v1/schedule/getLongRangeScheduleInfo")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"service\":\"CIV9\",\"fromDate\":\"2026-02-06\",\"toDate\":\"2026-03-01\",\"weekStartDay\":\"M\"}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].longRangeSchedule[0].portName").value("NHAVA SHEVA,MUMBAI"))
                .andExpect(jsonPath("$[0].longRangeSchedule[0].maxTeu").value(900))
                .andExpect(jsonPath("$[0].longRangeSchedule[0].callStatus").value("S"))
                .andExpect(jsonPath("$[0].longRangeSchedule[0].scheduleWeek").value(6));
    }

    @Test
    void invalidJwtIsUnauthorized() throws Exception {
        when(jwtUtil.validateTokenWithSession(anyString()))
                .thenThrow(new RuntimeException("JWT token has expired"));

        mockMvc.perform(post("/api/v1/schedule/getLongRangeScheduleInfo")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"service\":\"CIV9\",\"fromDate\":\"2026-02-06\",\"toDate\":\"2026-03-01\"}]"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.statusCode").value(401))
                .andExpect(jsonPath("$.errors[0].message").value("JWT token has expired"));
    }

    @Test
    void liveFailureUsesTheServiceStatus() throws Exception {
        when(jwtUtil.validateTokenWithSession(anyString())).thenReturn("tester");
        when(longRangeScheduleService.fetch(anyList(), anyString()))
                .thenThrow(new LongRangeScheduleException(HttpStatus.SERVICE_UNAVAILABLE, "Long range schedule service is unavailable"));

        mockMvc.perform(post("/api/v1/schedule/getLongRangeScheduleInfo")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"service\":\"CIV9\",\"fromDate\":\"2026-02-06\",\"toDate\":\"2026-03-01\"}]"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.statusCode").value(503))
                .andExpect(jsonPath("$.errors[0].message").value("Long range schedule service is unavailable"));
    }
}
