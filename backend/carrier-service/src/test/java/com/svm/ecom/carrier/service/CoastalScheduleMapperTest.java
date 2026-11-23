package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svm.ecom.carrier.enums.CallStatus;
import com.svm.ecom.carrier.enums.CallType;
import com.svm.ecom.carrier.model.CoastalScheduleCall;
import com.svm.ecom.common.enums.Bound;
import com.svm.ecom.common.enums.YesNoFlag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoastalScheduleMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void mapsLiveRowIncludingPivotFields() throws Exception {
        JsonNode funResponse = objectMapper.readTree("["
                + "{"
                + "\"serviceCode\":\"EGSC\","
                + "\"serviceName\":\"EVERGREEN\","
                + "\"vesselCode\":\"EGSV\","
                + "\"vesselName\":\"EVERGREEN VESSEL\","
                + "\"vesselImoNumber\":\"6556464\","
                + "\"voyageNumber\":\"03301\","
                + "\"pfBound\":\"W\","
                + "\"bound\":\"W\","
                + "\"pivotVoyage1\":\"612\","
                + "\"pivotBound1\":\"N\","
                + "\"pivotVoyage2\":\"\","
                + "\"pivotBound2\":\"\","
                + "\"pivotVoyage3\":\"\","
                + "\"pivotBound3\":\"\","
                + "\"callOrder\":\"3\","
                + "\"cycleNumber\":\"1\","
                + "\"subCycleNumber\":\"1\","
                + "\"portCode\":\"SGSIN\","
                + "\"portName\":\"SINGAPORE\","
                + "\"terminalCode\":\"SGSINPSA00\","
                + "\"terminalName\":\"PSA1\","
                + "\"callType\":\"C\","
                + "\"groupCallId\":\"GrpCall-573235\","
                + "\"arrPilot\":\"2026-06-10T03:46:00+08:00\","
                + "\"arrDock\":\"2026-06-10T05:46:00+08:00\","
                + "\"depDock\":\"2026-06-10T17:46:00+08:00\","
                + "\"depPilot\":\"2026-06-10T19:46:00+08:00\","
                + "\"terminalTime\":\"12.0\","
                + "\"remarks\":\"\","
                + "\"status\":\"Sailed/Confirmed Call\","
                + "\"arrivalReported\":\"N\""
                + "}"
                + "]");

        List<CoastalScheduleCall> calls = CoastalScheduleMapper.mapCalls(funResponse, objectMapper);

        assertEquals(1, calls.size());
        CoastalScheduleCall call = calls.get(0);
        assertEquals("SGSIN", call.getPortCode());
        assertEquals("SINGAPORE", call.getPortName());
        assertEquals(Double.valueOf(12.0d), call.getTerminalTime());
        assertEquals(CallStatus.S, call.getStatus());
        assertEquals("Completed", call.getStatusFlag());
        assertEquals(CallType.C, call.getCallType());
        assertEquals(YesNoFlag.N, call.getArrivalReported());
        assertEquals(Bound.W, call.getBound());
        assertEquals(Bound.W, call.getPfBound());
        assertEquals("612", call.getPivotVoyage1());
        assertEquals(Bound.N, call.getPivotBound1());
        assertEquals("", call.getPivotVoyage2());
        assertNull(call.getPivotBound2());
        assertEquals("", call.getPivotVoyage3());
        assertNull(call.getPivotBound3());
        assertEquals("", call.getRemarks());
        assertEquals(java.time.OffsetDateTime.parse("2026-06-10T03:46:00+08:00"), call.getArrPilot());

        String json = objectMapper.writeValueAsString(call);
        assertTrue(json.contains("pivotVoyage1"));
        assertTrue(json.contains("pivotBound1"));
        assertTrue(json.contains("pivotVoyage2"));
        assertTrue(json.contains("pivotBound2"));
        assertTrue(json.contains("pivotVoyage3"));
        assertTrue(json.contains("pivotBound3"));

        assertTrue(json.contains("statusFlag"));
        assertFalse(json.contains("callOrder"));
        assertFalse(json.contains("cycleNumber"));
        assertFalse(json.contains("subCycleNumber"));
        assertFalse(json.contains("statusFlag"));
        assertFalse(json.contains("PortName"));
    }

    @Test
    void emptyNumberBecomesNullAndKnownStatusCodesStayShort() {
        assertNull(CoastalScheduleMapper.mapStatus(""));
        assertNull(CoastalScheduleMapper.mapStatus(null));
        assertEquals(CallStatus.S, CoastalScheduleMapper.mapStatus("s"));
        assertEquals(CallStatus.N, CoastalScheduleMapper.mapStatus("Normal call"));
        assertEquals(CallStatus.D, CoastalScheduleMapper.mapStatus("Omitted call"));
        assertEquals(CallStatus.S, CoastalScheduleMapper.mapStatus("Sailed/Confirmed Call"));
    }
}
