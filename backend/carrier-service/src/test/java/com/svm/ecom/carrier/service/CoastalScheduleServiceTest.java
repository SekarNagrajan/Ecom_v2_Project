package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.carrier.config.CarrierApiProperties;
import com.svm.ecom.carrier.model.CoastalScheduleBlock;
import com.svm.ecom.carrier.model.CoastalScheduleRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CoastalScheduleServiceTest {

    private CoastalScheduleService service;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        CarrierApiProperties properties = new CarrierApiProperties();
        properties.setCoastalBaseUrl("https://lrpv2.solverminds.net");
        properties.setCoastalToken("live-token");
        properties.setMaxRetries(1);
        properties.setRetryDelay(0);
        service = new CoastalScheduleService(properties, new ObjectMapper().findAndRegisterModules());
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        service.setRestTemplate(restTemplate);
    }

    @Test
    void postsLiveBodyAndMapsFunResponse() {
        server.expect(requestTo("https://lrpv2.solverminds.net/vdsmgr/api/svm/getscheduledetail"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(headerDoesNotExist("Authorization"))
                .andExpect(header("Token", "live-token"))
                .andExpect(content().json("{\"values\":{\"service\":\"EGSC\",\"vessel\":\"EGSV\",\"fromdate\":\"2026-06-01\",\"todate\":\"2026-06-30\"}}"))
                .andRespond(withSuccess("{\"status\":200,\"FUNRESPONSE\":[{"
                        + "\"portCode\":\"SGSIN\",\"portName\":\"SINGAPORE\",\"bound\":\"W\","
                        + "\"callOrder\":\"3\",\"cycleNumber\":\"1\",\"subCycleNumber\":\"1\","
                        + "\"terminalTime\":\"12.0\",\"status\":\"Sailed/Confirmed Call\","
                        + "\"pivotVoyage1\":\"454353\",\"pivotBound1\":\"E\""
                        + "}]}", MediaType.APPLICATION_JSON));

        List<CoastalScheduleBlock> blocks = service.fetch(Collections.singletonList(request()), "tester");

        assertEquals(1, blocks.size());
        assertEquals(1, blocks.get(0).getCoastalSchedule().size());
        assertEquals("SINGAPORE", blocks.get(0).getCoastalSchedule().get(0).getPortName());
        assertEquals(com.svm.ecom.carrier.enums.CallStatus.S, blocks.get(0).getCoastalSchedule().get(0).getStatus());
        server.verify();
    }

    @Test
    void emptyFunResponseIsAnEmptyCoastalSchedule() {
        server.expect(requestTo("https://lrpv2.solverminds.net/vdsmgr/api/svm/getscheduledetail"))
                .andRespond(withSuccess("{\"status\":200,\"FUNRESPONSE\":[]}", MediaType.APPLICATION_JSON));

        List<CoastalScheduleBlock> blocks = service.fetch(Collections.singletonList(request()), "tester");

        assertEquals(1, blocks.size());
        assertTrue(blocks.get(0).getCoastalSchedule().isEmpty());
        server.verify();
    }

    @Test
    void rejectsFromDateAfterToDateBeforeCallingLiveApi() {
        CoastalScheduleRequest request = request();
        request.setFromDate(java.time.LocalDate.parse("2026-07-01"));
        request.setToDate(java.time.LocalDate.parse("2026-06-01"));

        CoastalScheduleException error = assertThrows(CoastalScheduleException.class,
                () -> service.fetch(Collections.singletonList(request), "tester"));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        server.verify();
    }

    @Test
    void upstreamNotFoundBecomes404() {
        server.expect(requestTo("https://lrpv2.solverminds.net/vdsmgr/api/svm/getscheduledetail"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        CoastalScheduleException error = assertThrows(CoastalScheduleException.class,
                () -> service.fetch(Collections.singletonList(request()), "tester"));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
    }

    private CoastalScheduleRequest request() {
        CoastalScheduleRequest request = new CoastalScheduleRequest();
        request.setService("EGSC");
        request.setVessel("EGSV");
        request.setFromDate(java.time.LocalDate.parse("2026-06-01"));
        request.setToDate(java.time.LocalDate.parse("2026-06-30"));
        return request;
    }
}
