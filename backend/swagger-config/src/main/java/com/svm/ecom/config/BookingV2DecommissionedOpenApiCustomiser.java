package com.svm.ecom.config;

import com.svm.ecom.security.decommissioned.BookingV2DecommissionedEndpointSupport;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomiser;

/**
 * Removes decommissioned Booking V2 CRUD paths from generated OpenAPI documents
 * (Swagger UI, Postman import, customer-private group).
 */
public class BookingV2DecommissionedOpenApiCustomiser implements OpenApiCustomiser {

    @Override
    public void customise(OpenAPI openApi) {
        if (openApi.getPaths() == null) {
            return;
        }
        BookingV2DecommissionedEndpointSupport.OPENAPI_PATHS_TO_REMOVE.forEach(openApi.getPaths()::remove);
    }
}
