package com.svm.ecom.carrier.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Long range schedule rows for one request")
public class LongRangeScheduleBlock {

    @Schema(description = "Schedule rows. Empty when the live report has no rows for this request.")
    private List<LongRangeScheduleCall> longRangeSchedule = new ArrayList<LongRangeScheduleCall>();

    public LongRangeScheduleBlock() {
    }

    public LongRangeScheduleBlock(List<LongRangeScheduleCall> longRangeSchedule) {
        this.longRangeSchedule = longRangeSchedule;
    }

    public List<LongRangeScheduleCall> getLongRangeSchedule() {
        return longRangeSchedule;
    }

    public void setLongRangeSchedule(List<LongRangeScheduleCall> longRangeSchedule) {
        this.longRangeSchedule = longRangeSchedule;
    }
}
