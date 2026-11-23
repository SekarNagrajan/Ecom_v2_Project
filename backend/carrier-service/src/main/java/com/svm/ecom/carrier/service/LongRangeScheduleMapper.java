package com.svm.ecom.carrier.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.svm.ecom.carrier.model.LongRangeScheduleCall;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps live longRangeSchedule rows to the IAD shape.
 * Extra live keys are ignored because {@link LongRangeScheduleCall} only declares the IAD fields.
 * Blank numeric/date/enum text fields become null.
 */
public final class LongRangeScheduleMapper {

    private LongRangeScheduleMapper() {
    }

    public static List<LongRangeScheduleCall> mapCalls(JsonNode rows, ObjectMapper objectMapper) {
        List<LongRangeScheduleCall> calls = new ArrayList<LongRangeScheduleCall>();
        if (rows == null || !rows.isArray()) {
            return calls;
        }
        for (JsonNode row : rows) {
            if (row != null && row.isObject()) {
                calls.add(mapCall(row, objectMapper));
            }
        }
        return calls;
    }

    private static LongRangeScheduleCall mapCall(JsonNode row, ObjectMapper objectMapper) {
        clearBlankText(row, "maxTeu", "scheduleWeek", "arrivalPilot", "arrivalDock",
                "departureDock", "departurePilot", "bound", "callStatus");
        return objectMapper.convertValue(row, LongRangeScheduleCall.class);
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
