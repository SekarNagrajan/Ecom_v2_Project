package com.svm.ecom.common.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Generic list wrapper for summary responses. Subclasses define the JSON property name.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryListResponse<T> {

    @JsonIgnore // important: subclasses expose their own @JsonProperty getter with custom name
    private List<T> items;

    public SummaryListResponse() {}

    public SummaryListResponse(List<T> items) {
        this.items = items;
    }

    protected List<T> getItems() {
        return items;
    }

    protected void setItems(List<T> items) {
        this.items = items;
    }
}
