package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.carrier.model.LongRangeScheduleCall;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LongRangeScheduleMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void mapsNumbersAndKeepsBlankVoyageRow() throws Exception {
        JsonNode rows = objectMapper.readTree("["
                + "{"
                + "\"serviceCode\":\"CIV9\",\"serviceName\":\"TESTING SERVICE\",\"proformaCode\":\"CIV9PRFSCH001\","
                + "\"vesselCode\":\"ACA\",\"vesselName\":\"ALL CARGO ARATHI\",\"vesselImo\":\"A121211\","
                + "\"maxTeu\":\"900\",\"voyageNumber\":\"107\",\"cycleNumber\":\"4\",\"bound\":\"S\","
                + "\"portCode\":\"INNSA\",\"portName\":\"NHAVA SHEVA,MUMBAI\","
                + "\"terminalCode\":\"INNSABMCT0\",\"terminalName\":\"BHARAT MUMBAI CONTAINER TERMINAL\","
                + "\"callOrder\":\"0\","
                + "\"arrivalPilot\":\"2026-02-06T17:17:00+05:30\",\"arrivalDock\":\"2026-02-06T19:17:00+05:30\","
                + "\"departureDock\":\"2026-02-07T10:17:00+05:30\",\"departurePilot\":\"2026-02-07T12:17:00+05:30\","
                + "\"callStatus\":\"S\",\"remarks\":\"\",\"scheduleWeek\":\"6\",\"scheduleMonthYear\":\"Feb-2026\""
                + "},"
                + "{"
                + "\"serviceCode\":\"CIV9\",\"serviceName\":\"TESTING SERVICE\",\"proformaCode\":\"CIV9PRFSCH001\","
                + "\"vesselCode\":\"\",\"vesselName\":\"\",\"vesselImo\":\"\",\"maxTeu\":\"\","
                + "\"voyageNumber\":\"108\",\"cycleNumber\":\"13\",\"bound\":\"\","
                + "\"portCode\":\"\",\"portName\":\"\",\"terminalCode\":\"\",\"terminalName\":\"\","
                + "\"callOrder\":\"\",\"arrivalPilot\":\"\",\"arrivalDock\":\"\",\"departureDock\":\"\",\"departurePilot\":\"\","
                + "\"callStatus\":\"\",\"remarks\":\"Blank voyage\",\"scheduleWeek\":\"\",\"scheduleMonthYear\":\"\""
                + "}"
                + "]");

        List<LongRangeScheduleCall> calls = LongRangeScheduleMapper.mapCalls(rows, objectMapper);

        assertEquals(2, calls.size());
        LongRangeScheduleCall first = calls.get(0);
        assertEquals(Integer.valueOf(900), first.getMaxTeu());
        assertEquals(Integer.valueOf(6), first.getScheduleWeek());
        assertEquals(com.svm.ecom.carrier.enums.CallStatus.S, first.getCallStatus());
        assertEquals("NHAVA SHEVA,MUMBAI", first.getPortName());
        assertEquals(com.svm.ecom.common.enums.Bound.S, first.getBound());
        assertEquals(java.time.OffsetDateTime.parse("2026-02-06T17:17:00+05:30"), first.getArrivalPilot());

        LongRangeScheduleCall blank = calls.get(1);
        assertEquals("Blank voyage", blank.getRemarks());
        assertEquals("108", blank.getVoyageNumber());
        assertNull(blank.getMaxTeu());
        assertNull(blank.getScheduleWeek());
        assertNull(blank.getCallStatus());
        assertNull(blank.getBound());
        assertNull(blank.getArrivalPilot());
    }
}


