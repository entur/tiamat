package org.rutebanken.tiamat.ext.fintraffic.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.rutebanken.tiamat.ext.fintraffic.FintrafficConstants;
import org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Profile("fintraffic-read-api")
@Controller
@Tag(name = "Read API")
public class FintrafficApiController {
    private final ReadApiNetexPublicationDeliveryService readApiNetexPublicationDeliveryService;

    public FintrafficApiController(
            ReadApiNetexPublicationDeliveryService readApiNetexPublicationDeliveryService
    ) {
        this.readApiNetexPublicationDeliveryService = readApiNetexPublicationDeliveryService;
    }

    @Operation(
            summary = "Get stop places, parkings and related entities as NeTEx Nordic",
            description = "Without a parameter, the API returns all current entities. "
                    + "The API joins the values of one parameter with OR, and joins different parameters with AND. "
                    + "Each parameter takes several values as a repeated parameter."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "A NeTEx Nordic PublicationDelivery document.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_XML_VALUE,
                            schema = @Schema(
                                    name = "PublicationDelivery",
                                    type = "object",
                                    description = "The NeTEx Nordic PublicationDelivery root element. "
                                            + "Examples of the NeTEx Nordic profile: https://github.com/entur/profile-examples/tree/master/netex"
                            ),
                            examples = @ExampleObject(
                                    name = "PublicationDelivery",
                                    value = ReadApiOpenApiExamples.PUBLICATION_DELIVERY
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A parameter value is not valid.",
                    content = @Content
            )
    })
    @GetMapping("/fintraffic/v1/stops")
    public void getNetexStream(
            @Parameter(
                    name = "transportModes",
                    in = ParameterIn.QUERY,
                    description = "Return only stop places with one of these transport modes.",
                    style = ParameterStyle.FORM,
                    explode = Explode.TRUE,
                    array = @ArraySchema(schema = @Schema(
                            type = "string",
                            allowableValues = {"air", "bus", "coach", "ferry", "metro", "rail", "trolleyBus",
                                    "tram", "water", "cableway", "funicular", "lift", "other"}
                    ))
            )
            @RequestParam(value = "transportModes", required = false) String[] transportMode,
            @Parameter(
                    name = "areaCodes",
                    in = ParameterIn.QUERY,
                    description = "Return only stop places and parkings of these competent transport authorities. "
                            + "The response always holds all topographic places and fare zones. "
                            + "A code has three upper-case letters, for example HSL.",
                    style = ParameterStyle.FORM,
                    explode = Explode.TRUE,
                    array = @ArraySchema(schema = @Schema(
                            type = "string",
                            pattern = "^" + FintrafficConstants.AREA_CODE_REGEX + "$",
                            example = "HSL"
                    ))
            )
            @RequestParam(value = "areaCodes", required = false) String[] areaCode,
            @Parameter(
                    name = "municipalityCodes",
                    in = ParameterIn.QUERY,
                    description = "Return only stop places and parkings in these municipalities. "
                            + "The response always holds all topographic places and fare zones. "
                            + "A code is the municipality code of Statistics Finland, for example 091 for Helsinki.",
                    style = ParameterStyle.FORM,
                    explode = Explode.TRUE,
                    array = @ArraySchema(schema = @Schema(
                            type = "string",
                            pattern = "^" + FintrafficConstants.MUNICIPALITY_CODE_REGEX + "$",
                            example = "091"
                    ))
            )
            @RequestParam(value = "municipalityCodes", required = false) String[] municipalityCode,
            @Parameter(hidden = true) HttpServletResponse response
    ) {
        try {
            validateTransportModes(transportMode);
            FintrafficConstants.validateAreaCodes(areaCode);
            FintrafficConstants.validateMunicipalityCodes(municipalityCode);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setBufferSize(32 * 1024); // 32 KB buffer size for efficient streaming

        try (OutputStream outputStream = response.getOutputStream()) {
            FintrafficReadApiSearchKey searchKey = new FintrafficReadApiSearchKey(transportMode, areaCode, municipalityCode);
            readApiNetexPublicationDeliveryService.streamPublicationDelivery(searchKey, outputStream);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void validateTransportModes(String[] transportModes) {
        if (transportModes != null) {
            for (String mode : transportModes) {
                VehicleModeEnumeration.fromValue(mode);
            }
        }
    }
}
