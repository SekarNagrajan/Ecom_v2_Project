package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.carrier.config.CarrierApiProperties;
import com.svm.ecom.carrier.model.LongRangeScheduleBlock;
import com.svm.ecom.carrier.model.LongRangeScheduleRequest;
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

class LongRangeScheduleServiceTest {

    private LongRangeScheduleService service;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        CarrierApiProperties properties = new CarrierApiProperties();
        properties.setCoastalBaseUrl("https://lrpv2.solverminds.net");
        properties.setLongRangeToken("live-token");
        properties.setMaxRetries(1);
        properties.setRetryDelay(0);
        service = new LongRangeScheduleService(properties, new ObjectMapper().findAndRegisterModules());
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        service.setRestTemplate(restTemplate);
    }

    @Test
    void postsLiveBodyAndMapsLongRangeSchedule() {
        server.expect(requestTo("https://lrpv2.solverminds.net/vdsmgr/api/svm/getlongrangescheduleinfo"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(headerDoesNotExist("Authorization"))
                .andExpect(header("Token", "live-token"))
                .andExpect(content().json("{\"values\":{\"service\":\"CIV9\",\"fromDate\":\"2026-02-06\",\"toDate\":\"2026-03-01\",\"weekStartDay\":\"M\"}}"))
                .andRespond(withSuccess("{\"status\":200,\"FUNRESPONSE\":[{\"longRangeSchedule\":[{"
                        + "\"serviceCode\":\"CIV9\",\"portName\":\"NHAVA SHEVA,MUMBAI\","
                        + "\"maxTeu\":\"900\",\"cycleNumber\":\"4\",\"callOrder\":\"1\",\"scheduleWeek\":\"6\","
                        + "\"callStatus\":\"N\",\"scheduleMonthYear\":\"Feb-2026\""
                        + "}]}]}", MediaType.APPLICATION_JSON));

        List<LongRangeScheduleBlock> blocks = service.fetch(Collections.singletonList(request(null)), "tester");

        assertEquals(1, blocks.size());
        assertEquals(1, blocks.get(0).getLongRangeSchedule().size());
        assertEquals(Integer.valueOf(900), blocks.get(0).getLongRangeSchedule().get(0).getMaxTeu());
        assertEquals(com.svm.ecom.carrier.enums.CallStatus.N, blocks.get(0).getLongRangeSchedule().get(0).getCallStatus());
        assertEquals("Feb-2026", blocks.get(0).getLongRangeSchedule().get(0).getScheduleMonthYear());
        server.verify();
    }

    @Test
    void emptyScheduleIsAnEmptyList() {
        server.expect(requestTo("https://lrpv2.solverminds.net/vdsmgr/api/svm/getlongrangescheduleinfo"))
                .andRespond(withSuccess("{\"status\":200,\"FUNRESPONSE\":[{\"longRangeSchedule\":[]}]}", MediaType.APPLICATION_JSON));

        List<LongRangeScheduleBlock> blocks = service.fetch(Collections.singletonList(request("S")), "tester");

        assertEquals(1, blocks.size());
        assertTrue(blocks.get(0).getLongRangeSchedule().isEmpty());
        server.verify();
    }

    @Test
    void upstreamTooManyRequestsStays429() {
        server.expect(requestTo("https://lrpv2.solverminds.net/vdsmgr/api/svm/getlongrangescheduleinfo"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS).body("Count Exceeded"));

        LongRangeScheduleException error = assertThrows(LongRangeScheduleException.class,
                () -> service.fetch(Collections.singletonList(request("M")), "tester"));

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, error.getStatus());
        assertEquals("Count Exceeded", error.getMessage());
    }

    @Test
    void rejectsUnknownWeekStartBeforeCallingLiveApi() {
        LongRangeScheduleRequest request = request("X");

        LongRangeScheduleException error = assertThrows(LongRangeScheduleException.class,
                () -> service.fetch(Collections.singletonList(request), "tester"));

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        server.verify();
    }

    private LongRangeScheduleRequest request(String weekStartDay) {
        LongRangeScheduleRequest request = new LongRangeScheduleRequest();
        request.setService("CIV9");
        request.setFromDate(java.time.LocalDate.parse("2026-02-06"));
        request.setToDate(java.time.LocalDate.parse("2026-03-01"));
        request.setWeekStartDay(weekStartDay);
        return request;
    }
}
