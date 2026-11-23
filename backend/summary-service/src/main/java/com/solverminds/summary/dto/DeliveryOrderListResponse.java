package com.solverminds.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.svm.ecom.common.api.SummaryListResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "DeliveryOrderListResponse", description = "List wrapper for DeliveryOrderDto")
public class DeliveryOrderListResponse extends SummaryListResponse<DeliveryOrderDto> {

    public DeliveryOrderListResponse() { super(); }

    public DeliveryOrderListResponse(List<DeliveryOrderDto> items) { super(items); }

    // Expose a domain-specific JSON property name instead of "items"
    @ArraySchema(arraySchema = @Schema(description = "List of delivery orders"),
                 schema = @Schema(implementation = DeliveryOrderDto.class))
    @JsonProperty("deliveryOrders")
    public List<DeliveryOrderDto> getDeliveryOrders() {
        return super.getItems();
    }

    @JsonProperty("deliveryOrders")
    public void setDeliveryOrders(List<DeliveryOrderDto> value) {
        super.setItems(value);
    }
}
