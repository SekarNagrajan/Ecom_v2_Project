package com.solverminds.booking.dto.v2.vgm.row;

import lombok.Data;

@Data
public class BookingCargoRow {
    private String bookNo;
    private String eqpid;
    private String eqptypeid;
    private String agencyid;
    private String originportid;
    private String finalportid;
    private String loadportid;
    private String dischargeportid;
    private String shipperown;
    private String isdummy;
}
