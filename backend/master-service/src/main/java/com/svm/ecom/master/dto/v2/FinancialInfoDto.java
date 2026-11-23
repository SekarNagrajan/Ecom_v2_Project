package com.svm.ecom.master.dto.v2;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * Financial information. Same documentation style as client request standard.
 * Dates use string ($date-time), example: 2025-09-10T00:00:00Z.
 */
@Schema(name = "FinancialInfo", description = "Represents financial information for the customer (credit type, currency, validity period, credit days and limit).",
        example = "{\"creditType\":\"TotalCredit\",\"currency\":\"USD\",\"validFrom\":\"2023-01-02T18:30:00Z\",\"validTo\":\"2031-12-30T18:30:00Z\",\"creditDays\":30,\"creditLimit\":1.0}")
public class FinancialInfoDto {

    @Schema(description = "The type of credit arrangement.", example = "TotalCredit")
    private String creditType;
    @Schema(description = "The currency code for the credit (e.g. ISO 4217).", example = "USD")
    private String currency;
    @Schema(description = "Valid from date timestamp. Start of the validity period.", format = "date-time", example = "2023-01-02T18:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date validFrom;
    @Schema(description = "Valid to date timestamp. End of the validity period.", format = "date-time", example = "2031-12-30T18:30:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date validTo;
    @Schema(description = "Number of credit days allowed.", example = "30")
    private Integer creditDays;
    @Schema(description = "Credit limit amount.", example = "1.0")
    private Double creditLimit;

    public String getCreditType() { return creditType; }
    public void setCreditType(String creditType) { this.creditType = creditType; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Date getValidFrom() { return validFrom; }
    public void setValidFrom(Date validFrom) { this.validFrom = validFrom; }
    public Date getValidTo() { return validTo; }
    public void setValidTo(Date validTo) { this.validTo = validTo; }
    public Integer getCreditDays() { return creditDays; }
    public void setCreditDays(Integer creditDays) { this.creditDays = creditDays; }
    public Double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(Double creditLimit) { this.creditLimit = creditLimit; }
}
