package com.svm.ecom.rates.dto.mrg;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@JsonPropertyOrder({"items", "metadata"})
@Schema(name = "MrgListResponse", description = "Cursor-paginated list of MRG records")
public class MrgListResponse {

    @ArraySchema(arraySchema = @Schema(description = "MRG records, each with its header fields and a details array of rate lines"),
            schema = @Schema(implementation = MrgItem.class))
    private List<MrgItem> items = new ArrayList<MrgItem>();
    @Schema(description = "Pagination envelope")
    private MrgPageMetadata metadata;

    public MrgListResponse() {
    }

    public MrgListResponse(List<MrgItem> items, MrgPageMetadata metadata) {
        this.items = items != null ? items : new ArrayList<MrgItem>();
        this.metadata = metadata;
    }

    public List<MrgItem> getItems() { return items; }
    public void setItems(List<MrgItem> items) { this.items = items; }
    public MrgPageMetadata getMetadata() { return metadata; }
    public void setMetadata(MrgPageMetadata metadata) { this.metadata = metadata; }
}
