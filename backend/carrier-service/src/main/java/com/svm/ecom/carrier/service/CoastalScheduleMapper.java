package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.svm.ecom.carrier.enums.CallStatus;
import com.svm.ecom.carrier.model.CoastalScheduleCall;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps one live FUNRESPONSE row to one IAD port call.

 * Dropped live keys (callOrder, cycleNumber, subCycleNumber) are stripped.

 * Dropped live keys (statusFlag, callOrder, cycleNumber, subCycleNumber) are stripped.

 */
public final class CoastalScheduleMapper {

    private CoastalScheduleMapper() {
    }

    public static List<CoastalScheduleCall> mapCalls(JsonNode funResponse, ObjectMapper objectMapper) {
        List<CoastalScheduleCall> calls = new ArrayList<CoastalScheduleCall>();
        if (funResponse == null || !funResponse.isArray()) {
            return calls;
        }
        for (JsonNode row : funResponse) {
            if (row != null && row.isObject()) {
                calls.add(mapCall(row, objectMapper));
            }
        }
        return calls;
    }

    /** Maps live status text to {@link CallStatus}. Kept for unit tests / reuse. */
    public static CallStatus mapStatus(String raw) {
        return CallStatus.fromValue(raw);
    }

    private static CoastalScheduleCall mapCall(JsonNode row, ObjectMapper objectMapper) {
        if (row instanceof ObjectNode) {
            ObjectNode object = (ObjectNode) row;
            // Dropped from IAD response — strip before bind so they never leak.
            object.remove("statusFlag");
            object.remove("callOrder");
            object.remove("cycleNumber");
            object.remove("subCycleNumber");
        }
        clearBlankText(row, "terminalTime", "arrPilot", "arrDock", "depDock", "depPilot",
                "pfBound", "bound", "pivotBound1", "pivotBound2", "pivotBound3",
                "callType", "status", "arrivalReported");
        return objectMapper.convertValue(row, CoastalScheduleCall.class);
    }

    private static void clearBlankText(JsonNode row, String... fields) {
        if (!(row instanceof ObjectNode)) {
            return;
        }
        ObjectNode object = (ObjectNode) row;
        for (String field : fields) {
            JsonNode value = object.get(field);
            if (value != null && value.isTextual() && value.asText().trim().isEmpty()) {
                object.putNull(field);
            }
        }
    }
}
