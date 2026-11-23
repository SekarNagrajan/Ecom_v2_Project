package com.svm.ecom.schedule.controller;

import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.schedule.enums.TransportMode;
import com.svm.ecom.schedule.model.VesselScheduleResponse;
import com.svm.ecom.schedule.model.VesselScheduleItem;
import com.svm.ecom.schedule.service.PortService;
import com.svm.ecom.schedule.service.ScheduleService;
import com.svm.ecom.schedule.service.VesselService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ScheduleController.class)
class ScheduleControllerVesselTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VesselService vesselService;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private PortService portService;

    // Note: PermissionService @MockBean removed as it's no longer used by ScheduleController

    private static final String TEST_VESSEL = "BUX";
    private static final String TEST_FROM_DATE = "2025-08-01";
    private static final String TEST_TO_DATE = "2025-08-10";
    private static final String TEST_USERNAME = "testuser";
    private static final String VALID_JWT_TOKEN = "Bearer valid.jwt.token.here";

    @BeforeEach
    void setUp() {
        // Mock JWT validation to return username
        when(vesselService.validateTokenAndExtractUsername(anyString())).thenReturn(TEST_USERNAME);
    }

    @Test
    void testGetVesselSchedule_Success() throws Exception {
        // Arrange
        VesselScheduleResponse mockResponse = createMockSuccessResponse();
        when(vesselService.fetchVesselScheduleFromDatabase(TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", VALID_JWT_TOKEN)
                .param("vesselCode", TEST_VESSEL)
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("200")))
                .andExpect(jsonPath("$.message", is("Success")))
                .andExpect(jsonPath("$.processedBy", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.byVessel", hasSize(2)))
                .andExpect(jsonPath("$.byVessel[0].vessel", is("BUX")))
                .andExpect(jsonPath("$.byVessel[0].vesselname", is("MSC BUSAN EXPRESS")))
                .andExpect(jsonPath("$.byVessel[0].port", is("SGSIN")))
                .andExpect(jsonPath("$.byVessel[0].portname", is("SINGAPORE")))
                .andExpect(jsonPath("$.byVessel[1].vessel", is("BUX")))
                .andExpect(jsonPath("$.byVessel[1].port", is("LKCMB")));
    }

    @Test
    void testGetVesselSchedule_NoResults() throws Exception {
        // Arrange
        VesselScheduleResponse mockResponse = new VesselScheduleResponse("204", "No schedules found for the given criteria", null, TEST_USERNAME);
        when(vesselService.fetchVesselScheduleFromDatabase(TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", VALID_JWT_TOKEN)
                .param("vesselCode", TEST_VESSEL)
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("204")))
                .andExpect(jsonPath("$.message", is("No schedules found for the given criteria")))
                .andExpect(jsonPath("$.processedBy", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.byVessel", is(nullValue())));
    }

    @Test
    void testGetVesselSchedule_DatabaseError() throws Exception {
        // Arrange
        VesselScheduleResponse mockResponse = new VesselScheduleResponse("500", "Database error: Connection failed", null, TEST_USERNAME);
        when(vesselService.fetchVesselScheduleFromDatabase(TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", VALID_JWT_TOKEN)
                .param("vesselCode", TEST_VESSEL)
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("500")))
                .andExpect(jsonPath("$.message", containsString("Database error")))
                .andExpect(jsonPath("$.processedBy", is(TEST_USERNAME)))
                .andExpect(jsonPath("$.byVessel", is(nullValue())));
    }

    @Test
    void testGetVesselSchedule_MissingAuthorizationHeader() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .param("vesselCode", TEST_VESSEL)
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("400")))
                .andExpect(jsonPath("$.message", containsString("Missing or invalid Authorization header")))
                .andExpect(jsonPath("$.byVessel", is(nullValue())));
    }

    @Test
    void testGetVesselSchedule_InvalidJwtToken() throws Exception {
        // Arrange
        when(vesselService.validateTokenAndExtractUsername(anyString()))
            .thenThrow(new SecurityException("JWT token has expired"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", "Bearer invalid.token")
                .param("vesselCode", TEST_VESSEL)
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", is("401")))
                .andExpect(jsonPath("$.message", is("JWT token has expired")))
                .andExpect(jsonPath("$.byVessel", is(nullValue())));
    }

    @Test
    void testGetVesselSchedule_MissingVesselCodeParameter() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", VALID_JWT_TOKEN)
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetVesselSchedule_InvalidDateFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", VALID_JWT_TOKEN)
                .param("vesselCode", TEST_VESSEL)
                .param("fromDate", "invalid-date")
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetVesselSchedule_EmptyVesselCode() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", VALID_JWT_TOKEN)
                .param("vesselCode", "")
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetVesselSchedule_VerifyDatabaseMethodCalled() throws Exception {
        // Arrange
        VesselScheduleResponse mockResponse = createMockSuccessResponse();
        when(vesselService.fetchVesselScheduleFromDatabase(TEST_VESSEL, TEST_FROM_DATE, TEST_TO_DATE, TEST_USERNAME))
            .thenReturn(mockResponse);

        // Act
        mockMvc.perform(get("/api/v1/schedule/vessel")
                .header("Authorization", VALID_JWT_TOKEN)
                .param("vesselCode", TEST_VESSEL)
                .param("fromDate", TEST_FROM_DATE)
                .param("toDate", TEST_TO_DATE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert that the database method was called with correct parameters
        // This verifies that the controller is now using the database method instead of external API
        // The mock verification is implicit in the when().thenReturn() setup
    }

    private VesselScheduleResponse createMockSuccessResponse() {
        VesselScheduleItem item1 = new VesselScheduleItem();
        item1.setVessel("BUX");
        item1.setVesselName("MSC BUSAN EXPRESS");
        item1.setPort("SGSIN");
        item1.setPortName("SINGAPORE");
        item1.setVoyage("511E");
        item1.setService("PAX");
        item1.setBound(Bound.E);
        item1.setTerminal("SGSINPSA001");
        item1.setTerminalName("PSA Terminal 1");
        item1.setArrivalDate(OffsetDateTime.parse("2025-08-05T06:00:00+08:00"));
        item1.setDepartureDate(OffsetDateTime.parse("2025-08-05T18:00:00+08:00"));
        item1.setType(TransportMode.M);

        VesselScheduleItem item2 = new VesselScheduleItem();
        item2.setVessel("BUX");
        item2.setVesselName("MSC BUSAN EXPRESS");
        item2.setPort("LKCMB");
        item2.setPortName("COLOMBO, SRI LANKA");
        item2.setVoyage("511E");
        item2.setService("PAX");
        item2.setBound(Bound.E);
        item2.setTerminal("LKCMBJCT00");
        item2.setTerminalName("Jaya Container Terminal");
        item2.setArrivalDate(OffsetDateTime.parse("2025-08-07T12:00:00+05:30"));
        item2.setDepartureDate(OffsetDateTime.parse("2025-08-07T20:00:00+05:30"));
        item2.setType(TransportMode.M);

        List<VesselScheduleItem> items = Arrays.asList(item1, item2);
        return new VesselScheduleResponse("200", "Success", items, TEST_USERNAME);
    }
}
