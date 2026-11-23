package com.solverminds.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Booking summary item returned only by GET /api/v1/bookings (system booking list).
 * Extends {@link BookingSummaryResponseDto} with the Agreement Party (AP) and Booking Party (BP)
 * customer code/name, so other endpoints sharing the base DTO are not affected.
 */
@Schema(description = "Booking summary item for the system booking list, including Agreement Party and Booking Party")
public class SystemBookingSummaryResponseDto extends BookingSummaryResponseDto {

    @JsonProperty("agreementPartyCustomerCode")
    @Schema(description = "Customer code of the Agreement Party", example = "2024003615", nullable = true)
    private String agreementPartyCustomerCode;

    @JsonProperty("agreementPartyCustomerName")
    @Schema(description = "Customer name of the Agreement Party", example = "TEST BEECOM TWS", nullable = true)
    private String agreementPartyCustomerName;

    @JsonProperty("bookingPartyCustomerCode")
    @Schema(description = "Customer code of the Booking Party", example = "2024003615", nullable = true)
    private String bookingPartyCustomerCode;

    @JsonProperty("bookingPartyCustomerName")
    @Schema(description = "Customer name of the Booking Party", example = "TEST BEECOM TWS", nullable = true)
    private String bookingPartyCustomerName;

    public SystemBookingSummaryResponseDto() {}

    public String getAgreementPartyCustomerCode() {
        return agreementPartyCustomerCode;
    }

    public void setAgreementPartyCustomerCode(String agreementPartyCustomerCode) {
        this.agreementPartyCustomerCode = agreementPartyCustomerCode;
    }

    public String getAgreementPartyCustomerName() {
        return agreementPartyCustomerName;
    }

    public void setAgreementPartyCustomerName(String agreementPartyCustomerName) {
        this.agreementPartyCustomerName = agreementPartyCustomerName;
    }

    public String getBookingPartyCustomerCode() {
        return bookingPartyCustomerCode;
    }

    public void setBookingPartyCustomerCode(String bookingPartyCustomerCode) {
        this.bookingPartyCustomerCode = bookingPartyCustomerCode;
    }

    public String getBookingPartyCustomerName() {
        return bookingPartyCustomerName;
    }

    public void setBookingPartyCustomerName(String bookingPartyCustomerName) {
        this.bookingPartyCustomerName = bookingPartyCustomerName;
    }

    @Override
    public String toString() {
        return "SystemBookingSummaryResponseDto{" +
                "base=" + super.toString() +
                ", agreementPartyCustomerCode='" + agreementPartyCustomerCode + '\'' +
                ", agreementPartyCustomerName='" + agreementPartyCustomerName + '\'' +
                ", bookingPartyCustomerCode='" + bookingPartyCustomerCode + '\'' +
                ", bookingPartyCustomerName='" + bookingPartyCustomerName + '\'' +
                '}';
    }
}
