package com.solverminds.booking.dto.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Details about a booked route for V2 API.
 */
@Schema(name = "BookingRoute", description = "Details about a booked route")
public class BookingRouteV2 {

    @NotNull
    @Schema(description = "Route id", example = "12345")
    private Integer id;

    @NotEmpty
    @Valid
    @Schema(description = "Route legs")
    private List<BookingRouteLegV2> legs;

    public BookingRouteV2() {
    }

    public BookingRouteV2(Integer id, List<BookingRouteLegV2> legs) {
        this.id = id;
        this.legs = legs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<BookingRouteLegV2> getLegs() {
        return legs;
    }

    public void setLegs(List<BookingRouteLegV2> legs) {
        this.legs = legs;
    }
}

