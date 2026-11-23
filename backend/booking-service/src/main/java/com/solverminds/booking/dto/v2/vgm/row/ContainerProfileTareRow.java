package com.solverminds.booking.dto.v2.vgm.row;

import lombok.Builder;
import lombok.Value;

/**
 * Row from ECOM {@code containerprofile} used for VGM tare resolution (legacy {@code getecomtareweightforsi}).
 */
@Value
@Builder
public class ContainerProfileTareRow {
    String equipmenttype;
    /** Raw tare from DB; may contain commas. */
    String tarewt;
    String containerno;
    String shipperownedunit;
    String fleetgroup;
}
