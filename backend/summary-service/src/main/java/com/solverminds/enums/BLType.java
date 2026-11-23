package com.solverminds.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BL type enumeration.
 * AP - Agreement Party , BP - Booking Party , BY - Buyer , CN - Consignee ,
DL - Drop Off Location , FW - Forwarder , N1 - Notify1 , N2 - Notify2 , NT - Notify ,
PL - PickUp Location , SH - Shipper , SL - Seller
 */
@Schema(description = "BL type", enumAsRef = true)
public enum BLType {

    @Schema(description = "Agreement Party")
    AP("AP"),

    @Schema(description = "Booking Party")
    BP("BP"),

    @Schema(description = "Buyer")
    BY("BY"),

    @Schema(description = "Consignee")
    CN("CN"),

    @Schema(description = "Forwarder")
    FW("FW"),

    @Schema(description = "Notify1")
    N1("N1"),

    @Schema(description = "Notify2")
    N2("N2"),

    @Schema(description = "Notify")
    NT("NT"),

    @Schema(description = "Drop Off Location")
    DL("DL"),

    @Schema(description = "PickUp Location")
    PL("PL"),

    @Schema(description = "Shipper")
    SH("SH"),

    @Schema(description = "Seller")
    SL("SL");

    private final String value;

    BLType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public String getDescription() {
        switch (this) {
            case AP: return "Agreement Party";
            case BP: return "Booking Party";
            case BY: return "Buyer";
            case CN: return "Consignee";
            case FW: return "Forwarder";
            case N1: return "Notify1";
            case N2: return "Notify2";
            case NT: return "Notify";
            case DL: return "Drop Off Location";
            case PL: return "PickUp Location";
            case SH: return "Shipper";
            case SL: return "Seller";
            default: return value;
        }
    }

    /**
     * Create enum from string value
     */
    public static BLType fromValue(String value) {
        if (value == null) {
            return null;
        } 
        for (BLType type : BLType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid BLType value: " + value + ". Valid values are: AP, BP, BY, CN, FW, N1, N2, NT, DL, PL, SH, SL");
    }
}
