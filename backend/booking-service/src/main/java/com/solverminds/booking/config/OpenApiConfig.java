package com.solverminds.booking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration("bookingOpenApiConfig")
@OpenAPIDefinition(
    info = @Info(
        title = "Booking Service API",
        version = "2.0.0",
        description = "API for managing ecom booking data and summary information.\n\n" +
            "## API Versions\n" +
            "- **V1 API** (`/api/v1/booking/*`): Original API with custom endpoint naming\n" +
            "- **Bookings API** (`/api/bookings/*`): RESTful API following OpenAPI 3.0 conventions\n" +
            "- **Bookings V2** (`/api/v2/bookings/*`): Customer OpenAPI–aligned booking and VGM (Verified Gross Mass) operations\n" +
            "- **Bookings V3** (`/api/v3/bookings/*`): Per-container VGM (GET/PUT `/{bookingId}/vgm`)\n\n" +
            "## Bookings Endpoints\n" +
            "- `GET /api/bookings` - List bookings with pagination, sorting, and filtering\n" +
            "- `POST /api/bookings` - Create a new booking\n" +
            "- `GET /api/bookings/{bookingId}` - Get a booking by ID\n" +
            "- `PUT /api/bookings/{bookingId}` - Update a booking\n\n" +
            "## Bookings V2 (includes VGM)\n" +
            "- `GET /api/v2/bookings` - Paged list (authenticated)\n" +
            "- `POST /api/v2/bookings` - Create booking\n" +
            "- `GET /api/v2/bookings/{bookingId}` - Get booking by id\n" +
            "- `PUT /api/v2/bookings/{bookingId}` - Update booking\n" +
            "- `POST /api/v2/bookings/{bookingId}/cancel` - Cancel request\n" +
            "- `GET /api/v2/bookings/{bookingId}/vgm` - VGM details (ECOM header + containers; cargo from documentation `booking`)\n" +
            "- `PUT /api/v2/bookings/{bookingId}/vgm` - Create or replace VGM (ECOM `ecom_vgmsolas`, mirrored to documentation `vgmsolas`)\n\n" +
            "## Bookings V3 (per-container VGM)\n" +
            "- `GET /api/v3/bookings/{bookingId}/vgm` - VGM details with declarant fields on each container\n" +
            "- `PUT /api/v3/bookings/{bookingId}/vgm` - Create or replace per-container VGM (same `ecom_vgmsolas` / `vgmsolas` tables)",
        contact = @Contact(
            name = "SolverMinds Support",
            email = "support@solverminds.com"
        ),
        license = @License(
            name = "Proprietary",
            url = "https://solverminds.com/license"
        )
    ),
    servers = {
        @Server(url = "https://localhost:8081", description = "Local Development Server"),
        @Server(url = "https://api-dev.solverminds.com", description = "Development Server"),
        @Server(url = "https://api-uat.solverminds.com", description = "UAT Server"),
        @Server(url = "https://api.solverminds.com", description = "Production Server")
    },
    tags = {
        @Tag(name = "Booking", description = "V1 Booking service endpoints (legacy)"),
        @Tag(
            name = "Bookings",
            description = "V2 RESTful booking endpoints (`/api/v2/bookings`), including list/create/get/update/cancel and VGM (GET/PUT `/{bookingId}/vgm`)."
        ),
        @Tag(
            name = "Bookings V3",
            description = "V3 booking endpoints (`/api/v3/bookings`). VGM GET/PUT keep declarant fields on each container."
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "JWT authentication token required. Use 'Bearer <token>' format."
)
public class OpenApiConfig {
}
