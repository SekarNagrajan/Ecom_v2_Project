package com.svm.ecom.carrier.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Port calls for one coastal schedule request")
public class CoastalScheduleBlock {

    @Schema(description = "Port calls in call order. Empty when the live schedule has no rows for this request.")
    private List<CoastalScheduleCall> coastalSchedule = new ArrayList<CoastalScheduleCall>();

    public CoastalScheduleBlock() {
    }

    public CoastalScheduleBlock(List<CoastalScheduleCall> coastalSchedule) {
        this.coastalSchedule = coastalSchedule;
    }

    public List<CoastalScheduleCall> getCoastalSchedule() {
        return coastalSchedule;
    }

    public void setCoastalSchedule(List<CoastalScheduleCall> coastalSchedule) {
        this.coastalSchedule = coastalSchedule;
    }
}
