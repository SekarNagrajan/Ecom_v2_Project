package com.svm.ecom.master.controller;

import com.svm.ecom.master.api.VesselDoc;
import com.svm.ecom.master.api.PortDoc;
import com.svm.ecom.master.api.CountryDoc;
import com.svm.ecom.master.api.PackageDoc;
import com.svm.ecom.master.api.TerminalDoc;
import com.svm.ecom.master.model.CommodityView;
import com.svm.ecom.master.model.HSView;
import com.svm.ecom.master.model.DGView;
import com.svm.ecom.master.model.VesselView;
import com.svm.ecom.master.model.PortView;
import com.svm.ecom.master.model.CountryView;
import com.svm.ecom.master.model.PackageView;
import com.svm.ecom.master.model.EquipmentView;
import com.svm.ecom.master.model.TerminalView;
import com.svm.ecom.master.api.EquipmentDoc;
import com.svm.ecom.master.api.CommodityDoc;
import com.svm.ecom.master.api.HSDoc;
import com.svm.ecom.master.api.DGDoc;
import com.svm.ecom.master.service.MasterService;
// Swagger/OpenAPI imports
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Centralized documentation constants
import com.svm.ecom.docs.SwaggerDocs;
// Error response schema
import com.svm.ecom.dto.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for master data endpoints
 * Provides read-only access to vessel and port master data
 */
@RestController
@RequestMapping("/api/v1/master")
@Tag(name = "Master Data API", description = "Access to vessel and port master data")
public class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

    @Autowired
    private MasterService masterService;

    /**
     * Get all active vessels
     */
    @GetMapping("/vessels")
    @Operation(
        summary = SwaggerDocs.Master.SUMMARY_GET_VESSELS,
        description = SwaggerDocs.Master.DESC_GET_VESSELS,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = VesselDoc.class)),
                    examples = @ExampleObject(value = "[{\"vesselCode\":\"P01\",\"vesselName\":\"PANDA 001\",\"imoNumber\":\"9290787\",\"builtYear\":2015,\"builtMonth\":\"SEP\",\"status\":\"ACTIVE\",\"grossTonnage\":66280,\"loa\":276.2,\"breadth\":32.2,\"draft\":14.0,\"deadWeight\":68288,\"reeferPlugs\":500,\"capacityTeu\":5527,\"flag\":\"PORTUGAL\",\"callSign\":\"CQDD\"}]")
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_FORBIDDEN
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "NotFound",
                        value = SwaggerDocs.Examples.ERROR_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            )
        }
    )
    public ResponseEntity<List<VesselView>> getActiveVessels(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active vessels request received from IP: {}", clientIp);

        try {
            List<VesselView> vessels = masterService.getActiveVessels();
            logger.info("Successfully returned {} active vessels to IP: {}", vessels.size(), clientIp);
            return ResponseEntity.ok(vessels);

        } catch (Exception e) {
            logger.error("Error fetching active vessels from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * Get all active ports
     */
    @GetMapping("/ports")
    @Operation(
        summary = SwaggerDocs.Master.SUMMARY_GET_PORTS,
        description = SwaggerDocs.Master.DESC_GET_PORTS,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = SwaggerDocs.Common.SUCCESS_200,
                content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PortDoc.class)),
                    examples = @ExampleObject(value = "[{\"portCode\":\"SGSIN\",\"portName\":\"SINGAPORE\",\"portType\":\"P\",\"latitude\":1.2644,\"longitude\":103.8223,\"timezoneOffset\":\"+08:00\",\"countryCode\":\"SG\",\"isEur1875\":false},{\"portCode\":\"NLRTM\",\"portName\":\"ROTTERDAM\",\"portType\":\"P\",\"latitude\":51.9475,\"longitude\":4.1347,\"timezoneOffset\":\"+01:00\",\"countryCode\":\"NL\",\"isEur1875\":true}]")
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = SwaggerDocs.Common.BAD_REQUEST_400,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "ValidationError",
                        value = SwaggerDocs.Examples.ERROR_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = SwaggerDocs.Common.UNAUTHORIZED_401,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "Unauthorized",
                        value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED
                    )
                )
            ),
            @ApiResponse(
                responseCode = "403",
                description = SwaggerDocs.Common.FORBIDDEN_403,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "Forbidden",
                        value = SwaggerDocs.Examples.ERROR_FORBIDDEN
                    )
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = SwaggerDocs.Common.NOT_FOUND_404,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "NotFound",
                        value = SwaggerDocs.Examples.ERROR_VALIDATION
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = SwaggerDocs.Common.SERVER_ERROR_500,
                content = @Content(
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(
                        name = "InternalError",
                        value = SwaggerDocs.Examples.ERROR_INTERNAL
                    )
                )
            )
        }
    )
    public ResponseEntity<List<PortView>> getActivePorts(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active ports request received from IP: {}", clientIp);

        try {
            List<PortView> ports = masterService.getActivePorts();
            logger.info("Successfully returned {} active ports to IP: {}", ports.size(), clientIp);
            return ResponseEntity.ok(ports);

        } catch (Exception e) {
            logger.error("Error fetching active ports from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * Get all active countries
     */
    @GetMapping("/country")
    @Operation(summary = SwaggerDocs.Master.SUMMARY_GET_COUNTRY, description = SwaggerDocs.Master.DESC_GET_COUNTRY, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Common.SUCCESS_200, content = @Content(array = @ArraySchema(schema = @Schema(implementation = CountryDoc.class)), examples = @ExampleObject(value = "[{\"countryCode\":\"CN\",\"countryName\":\"China\"},{\"countryCode\":\"DE\",\"countryName\":\"Germany\"}]"))),
            @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL)))
    })
    public ResponseEntity<List<CountryView>> getActiveCountry(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active country request received from IP: {}", clientIp);

        try {
            List<CountryView> country = masterService.getActiveCountries();
            logger.info("Successfully returned {} active country to IP: {}", country.size(), clientIp);
            return ResponseEntity.ok(country);

        } catch (Exception e) {
            logger.error("Error fetching active country from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * Get all active packages
     */
    @GetMapping("/package")
    @Operation(summary = SwaggerDocs.Master.SUMMARY_GET_PACKAGE, description = SwaggerDocs.Master.DESC_GET_PACKAGE, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Common.SUCCESS_200, content = @Content(array = @ArraySchema(schema = @Schema(implementation = PackageDoc.class)), examples = @ExampleObject(value = "[{\"packageCode\":\"BOXS\",\"description\":\"Boxes\"},{\"packageCode\":\"PALL\",\"description\":\"Pallets\"}]"))),
            @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL)))
    })
    public ResponseEntity<List<PackageView>> getActivePackage(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active package request received from IP: {}", clientIp);

        try {
            List<PackageView> packages = masterService.getActivePackages();
            logger.info("Successfully returned {} active package to IP: {}", packages.size(), clientIp);
            return ResponseEntity.ok(packages);

        } catch (Exception e) {
            logger.error("Error fetching active package from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * Get all active Equipment
     */
    @GetMapping("/equipment")
    @Operation(summary = SwaggerDocs.Master.SUMMARY_GET_EQUIPMENT, description = SwaggerDocs.Master.DESC_GET_EQUIPMENT, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Common.SUCCESS_200, content = @Content(array = @ArraySchema(schema = @Schema(implementation = EquipmentDoc.class)), examples = @ExampleObject(value = "[{\"equipmentType\":\"20DC\",\"equipmentDescription\":\"STANDARD 20 DRY CONTAINER\"},{\"equipmentType\":\"40DC\",\"equipmentDescription\":\"STANDARD 40 DRY CONTAINER\"}]"))),
            @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL)))
    })
    public ResponseEntity<List<EquipmentView>> getActiveEquipment(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active equipment request received from IP: {}", clientIp);

        try {
            List<EquipmentView> equipment = masterService.getActiveEquipment();
            logger.info("Successfully returned {} active equipment to IP: {}", equipment.size(), clientIp);
            return ResponseEntity.ok(equipment);

        } catch (Exception e) {
            logger.error("Error fetching active package from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * Get all active Terminal
     */
    @GetMapping("/terminal")
    @Operation(summary = SwaggerDocs.Master.SUMMARY_GET_TERMINAL, description = SwaggerDocs.Master.DESC_GET_TERMINAL, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Common.SUCCESS_200, content = @Content(array = @ArraySchema(schema = @Schema(implementation = TerminalDoc.class)), examples = @ExampleObject(value = "[{\"terminalCode\":\"INMUNACT01\",\"terminalName\":\"ADANI CONTAINER TERMINAL\"},{\"terminalCode\":\"INHZAAHCT0\",\"terminalName\":\"ADANI HAZIRA CONTAINER TERMINAL\"}]"))),
            @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL)))
    })
    public ResponseEntity<List<TerminalView>> getActiveTerminal(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active terminal request received from IP: {}", clientIp);

        try {
            List<TerminalView> terminal = masterService.getActiveTerminal();
            logger.info("Successfully returned {} active terminal to IP: {}", terminal.size(), clientIp);
            return ResponseEntity.ok(terminal);

        } catch (Exception e) {
            logger.error("Error fetching active terminal from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * Extract client IP address from request
     * Handles various proxy headers to get real client IP
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Get all active Commodity
     */
    @GetMapping("/commodity")
    @Operation(summary = SwaggerDocs.Master.SUMMARY_GET_COMMODITY, description = SwaggerDocs.Master.DESC_GET_COMMODITY, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Common.SUCCESS_200, content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommodityDoc.class)), examples = @ExampleObject(value = "[{\"commodityCode\":\"01\",\"commodityName\":\"Animals; live\"},{\"commodityCode\":\"02\",\"commodityName\":\"Meat and edible meat offal\"}]"))),
            @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL)))
    })
    public ResponseEntity<List<CommodityView>> getActiveCommodity(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active commodity request received from IP: {}", clientIp);

        try {
            List<CommodityView> commodity = masterService.getActiveCommodity();
            logger.info("Successfully returned {} active commodity to IP: {}", commodity.size(), clientIp);
            return ResponseEntity.ok(commodity);
        } catch (Exception e) {
            logger.error("Error fetching active commodity from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    } 

    /**
     * Get all active HS
     */
    @GetMapping("/hslist")
    @Operation(summary = SwaggerDocs.Master.SUMMARY_GET_HS, description = SwaggerDocs.Master.DESC_GET_HS, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Common.SUCCESS_200, content = @Content(array = @ArraySchema(schema = @Schema(implementation = HSDoc.class)), examples = @ExampleObject(value = "[{\"hsCode\":\"0101210061\",\"hsName\":\"Pure-bred breeding horses\",\"commodityGroup\":\"01\"},{\"hsCode\":\"0101291061\",\"hsName\":\"Horses for slaughter\",\"commodityGroup\":\"01\"}]"))), 
            @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL)))
    })  
    public ResponseEntity<List<HSView>> getActiveHS(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active HS request received from IP: {}", clientIp);

        try {
            List<HSView> hs = masterService.getActiveHS();
            logger.info("Successfully returned {} active HS to IP: {}", hs.size(), clientIp);
            return ResponseEntity.ok(hs);
        } catch (Exception e) {
            logger.error("Error fetching active HS from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }

    /**
     * Get all active DG
     */
    @GetMapping("/dglist")
    @Operation(summary = SwaggerDocs.Master.SUMMARY_GET_DG, description = SwaggerDocs.Master.DESC_GET_DG, responses = {
            @ApiResponse(responseCode = "200", description = SwaggerDocs.Common.SUCCESS_200, content = @Content(array = @ArraySchema(schema = @Schema(implementation = DGDoc.class)), examples = @ExampleObject(value = "[{\"unNo\":\"0039\",\"classCode\":\"1.2G\",\"shippingName\":\"BOMBS, PHOTO-FLASH\"},{\"unNo\":\"0040\",\"classCode\":\"1.2G\",\"shippingName\":\"BOMBS, PHOTO-FLASH\"}]"))),
            @ApiResponse(responseCode = "400", description = SwaggerDocs.Common.BAD_REQUEST_400, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "ValidationError", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "401", description = SwaggerDocs.Common.UNAUTHORIZED_401, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Unauthorized", value = SwaggerDocs.Examples.ERROR_UNAUTHORIZED))),
            @ApiResponse(responseCode = "403", description = SwaggerDocs.Common.FORBIDDEN_403, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "Forbidden", value = SwaggerDocs.Examples.ERROR_FORBIDDEN))),
            @ApiResponse(responseCode = "404", description = SwaggerDocs.Common.NOT_FOUND_404, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "NotFound", value = SwaggerDocs.Examples.ERROR_VALIDATION))),
            @ApiResponse(responseCode = "500", description = SwaggerDocs.Common.SERVER_ERROR_500, content = @Content(schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "InternalError", value = SwaggerDocs.Examples.ERROR_INTERNAL)))
    })
    public ResponseEntity<List<DGView>> getActiveDG(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);
        logger.info("Active DG request received from IP: {}", clientIp);
            
        try {
            List<DGView> dg = masterService.getActiveDG();
            logger.info("Successfully returned {} active DG to IP: {}", dg.size(), clientIp);
            return ResponseEntity.ok(dg);
        } catch (Exception e) {
            logger.error("Error fetching active DG from IP: {} - {}", clientIp, e.getMessage(), e);
            throw e; // Let global exception handler deal with it
        }
    }
}
