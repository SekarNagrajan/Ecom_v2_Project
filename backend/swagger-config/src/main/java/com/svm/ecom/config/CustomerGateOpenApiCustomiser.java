package com.svm.ecom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.customizers.OpenApiCustomiser;

import java.util.Arrays;
import java.util.List;


public class CustomerGateOpenApiCustomiser implements OpenApiCustomiser {

    private static final List<String> UNUSED_V1_RESPONSES = Arrays.asList(
        "UnauthorizedError", "BadRequestError", "ForbiddenError", "NotFoundError",
        "BusinessRuleError", "ServerError", "MethodNotAllowedError");

    @Override
    public void customise(OpenAPI openApi) {
        Components components = openApi.getComponents();
        if (components == null || components.getResponses() == null) {
            return;
        }
        UNUSED_V1_RESPONSES.forEach(components.getResponses()::remove);
        if (components.getResponses().isEmpty()) {
            components.setResponses(null);
        }
    }
}
