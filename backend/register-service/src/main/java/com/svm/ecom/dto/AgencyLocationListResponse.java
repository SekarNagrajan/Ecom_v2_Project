package com.svm.ecom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "AgencyLocationListResponse", description = "Response containing all agency locations")
public class AgencyLocationListResponse {
    
    @Schema(description = "List of all agency locations")
    private List<AgencyLocationDTO> data;

    // Default constructor
    public AgencyLocationListResponse() {
    }

    // Constructor with data
    public AgencyLocationListResponse(List<AgencyLocationDTO> data) {
        this.data = data;
    }

    // Getters and Setters
    public List<AgencyLocationDTO> getData() {
        return data;
    }

    public void setData(List<AgencyLocationDTO> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AgencyLocationListResponse{" +
                "data=" + (data != null ? data.size() + " items" : "null") +
                '}';
    }
}
