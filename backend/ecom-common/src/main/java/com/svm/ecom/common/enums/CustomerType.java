package com.svm.ecom.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Customer/Party type enumeration.
 * Defines various party roles in shipping operations.
 */
@Schema(description = "Customer Type: AP – Agreement Party, BP – Booking Party, BY – Buyer, CN – Consignee, DL – DropOff Location, FW – Freight Forwarder, N1 – Notify Party 1, N2 – Notify Party 2, NC – Named Customer, NT – Notify, PL – PickUp Location, SH – Shipper, SL – Seller", enumAsRef = true)
public enum CustomerType {

    @Schema(description = "AP – Agreement Party")
    AP("AP"),

    @Schema(description = "BP – Booking Party")
    BP("BP"),

    @Schema(description = "BY – Buyer")
    BY("BY"),

    @Schema(description = "CN – Consignee")
    CN("CN"),

    @Schema(description = "DL – DropOff Location")
    DL("DL"),

    @Schema(description = "FW – Freight Forwarder")
    FW("FW"),

    @Schema(description = "N1 – Notify Party 1")
    N1("N1"),

    @Schema(description = "N2 – Notify Party 2")
    N2("N2"),

    @Schema(description = "NC – Named Customer")
    NC("NC"),

    @Schema(description = "NT – Notify")
    NT("NT"),

    @Schema(description = "PL – PickUp Location")
    PL("PL"),

    @Schema(description = "SH – Shipper")
    SH("SH"),

    @Schema(description = "SL – Seller")
    SL("SL");

    private final String value;

    CustomerType(String value) {
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
            case DL: return "DropOff Location";
            case FW: return "Freight Forwarder";
            case N1: return "Notify Party 1";
            case N2: return "Notify Party 2";
            case NC: return "Named Customer";
            case NT: return "Notify";
            case PL: return "PickUp Location";
            case SH: return "Shipper";
            case SL: return "Seller";
            default: return value;
        }
    }

    /**
     * Create enum from string value.
     * Handles null by returning null.
     */
    @JsonCreator
    public static CustomerType fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        for (CustomerType type : CustomerType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CustomerType value: " + value + 
            ". Valid values are: AP, BP, BY, CN, DL, FW, N1, N2, NC, NT, PL, SH, SL");
    }
}

