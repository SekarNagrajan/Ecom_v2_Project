package com.svm.ecom.master.controller;

import com.svm.ecom.master.enums.PortType;
import com.svm.ecom.master.model.VesselView;
import com.svm.ecom.master.model.PortView;
import com.svm.ecom.master.service.MasterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvc slice tests for MasterController
 * Tests controller layer with mocked service
 */
@WebMvcTest(MasterController.class)
class MasterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MasterService masterService;

    @Test
    void testGetActiveVessels_Success() throws Exception {
        // Given
        List<VesselView> mockVessels = Arrays.asList(
                createMockVessel("BUX", "BUNGA RAYA DUA", "997686907"),
                createMockVessel("KDS", "KOTA DELIMA", "7828245"));
        when(masterService.getActiveVessels()).thenReturn(mockVessels);

        // When & Then
        mockMvc.perform(get("/api/v1/master/vessels")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].vesselCode").value("BUX"))
                .andExpect(jsonPath("$[0].vesselName").value("BUNGA RAYA DUA"))
                .andExpect(jsonPath("$[0].imoNumber").value("997686907"))
                .andExpect(jsonPath("$[1].vesselCode").value("KDS"))
                .andExpect(jsonPath("$[1].vesselName").value("KOTA DELIMA"))
                .andExpect(jsonPath("$[1].imoNumber").value("7828245"));
    }

    @Test
    void testGetActivePorts_Success() throws Exception {
        // Given
        List<PortView> mockPorts = Arrays.asList(
                createMockPort("SGSIN", "SINGAPORE", "P", 1.2644, 103.8223, "+08:00", "SG", false),
                createMockPort("NLRTM", "ROTTERDAM", "P", 51.9475, 4.1347, "+01:00", "NL", true));
        when(masterService.getActivePorts()).thenReturn(mockPorts);

        // When & Then
        mockMvc.perform(get("/api/v1/master/ports")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].portCode").value("SGSIN"))
                .andExpect(jsonPath("$[0].portName").value("SINGAPORE"))
                .andExpect(jsonPath("$[0].portType").value("P"))
                .andExpect(jsonPath("$[0].latitude").value(1.2644))
                .andExpect(jsonPath("$[0].longitude").value(103.8223))
                .andExpect(jsonPath("$[0].timezoneOffset").value("+08:00"))
                .andExpect(jsonPath("$[0].countryCode").value("SG"))
                .andExpect(jsonPath("$[0].isEur1875").value(false))
                .andExpect(jsonPath("$[1].portCode").value("NLRTM"))
                .andExpect(jsonPath("$[1].portName").value("ROTTERDAM"))
                .andExpect(jsonPath("$[1].portType").value("P"))
                .andExpect(jsonPath("$[1].latitude").value(51.9475))
                .andExpect(jsonPath("$[1].longitude").value(4.1347))
                .andExpect(jsonPath("$[1].timezoneOffset").value("+01:00"))
                .andExpect(jsonPath("$[1].countryCode").value("NL"))
                .andExpect(jsonPath("$[1].isEur1875").value(true));
    }

    @Test
    void testGetActiveVessels_EmptyList() throws Exception {
        // Given
        when(masterService.getActiveVessels()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/master/vessels")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetActivePorts_EmptyList() throws Exception {
        // Given
        when(masterService.getActivePorts()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/v1/master/ports")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }



    /**
     * Helper method to create mock VesselView
     */
    private VesselView createMockVessel(String code, String name,String imoNumber) {
        return new VesselView() {
            @Override
            public String getVesselCode() {
                return code;
            }

            @Override
            public String getVesselName() {
                return name;
            }

            @Override
            public String getImoNumber() {
                return imoNumber;
            }

            @Override
            public String getBuiltYear() {
                return null;
            }

            @Override
            public String getBuiltMonth() {
                return null;
            }

            @Override
            public String getStatus() {
                return null;
            }

            @Override
            public Integer getGrossTonnage() {
                return null;
            }

            @Override
            public Double getLoa() {
                return null;
            }

            @Override
            public Double getBreadth() {
                return null;
            }

            @Override
            public Double getDraft() {
                return null;
            }

            @Override
            public Double getDeadWeight() {
                return null;
            }

            @Override
            public Integer getReeferPlugs() {
                return null;
            }

            @Override
            public Integer getCapacityTeu() {
                return null;
            }

            @Override
            public String getFlag() {
                return null;
            }

            @Override
            public String getCallSign() {
                return null;
            }
        };
    }

    /**
     * Helper method to create mock PortView
     */
    private PortView createMockPort(String code, String name, String type,
                                     Double lat, Double lon, String tzOffset, String country, boolean isEur1875) {
        return new PortView() {
            @Override
            public String getPortCode() {
                return code;
            }

            @Override
            public String getPortName() {
                return name;
            }

            @Override
            public PortType getPortType() {
                return PortType.fromValue(type);
            }

            @Override
            public Double getLatitude() {
                return lat;
            }

            @Override
            public Double getLongitude() {
                return lon;
            }

            @Override
            public String getTimezoneOffset() {
                return tzOffset;
            }

            @Override
            public String getCountryCode() {
                return country;
            }

            @Override
            public boolean isEur1875() {
                return isEur1875;
            }
        };
    }
}
