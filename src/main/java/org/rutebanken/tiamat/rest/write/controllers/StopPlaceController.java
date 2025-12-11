package org.rutebanken.tiamat.rest.write.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

@Tag(
    name = "Stop Places write API",
    description = "Write mono-modal StopPlace entities asynchronously."
)
interface StopPlaceController {
    @Operation(
        summary = "Creates a stop place from a Netex XML representation",
        description = """
        Accepts a StopPlacesDto containing a NeTEx StopPlace XML.
        Only a single mono-modal stop place is allowed.
        Returns a job representing the asynchronous creation process.
        """,
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/xml",
                examples = {
                    @ExampleObject(
                        name = "Create StopPlace Example",
                        value = """
                                                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                                                  <StopPlace id="MES:StopPlace:1" version="1">
                                                    <Name lang="akk">Bīt Mīt Uruk</Name>
                                                    <PrivateCode>1</PrivateCode>
                                                    <Centroid>
                                                      <Location>
                                                        <Longitude>45.638803</Longitude>
                                                        <Latitude>31.324350</Latitude>
                                                      </Location>
                                                    </Centroid>
                                                    <TransportMode>rail</TransportMode>
                                                    <StopPlaceType>railStation</StopPlaceType>
                                                    <Weighting>interchangeAllowed</Weighting>
                                                    <keyList>
                                                      <KeyValue>
                                                        <Key>owner</Key>
                                                        <Value>1</Value>
                                                      </KeyValue>
                                                    </keyList>
                                                  </StopPlace>
                                                </stopPlaces>
                        """
                    ),
                }
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "202",
                description = "Creation job submitted",
                content = @Content(
                    schema = @Schema(implementation = StopPlaceJobDto.class)
                )
            ),
        }
    )
    Response createStopPlace(StopPlacesDto stopPlacesDto);

    Response updateStopPlace(StopPlacesDto stopPlacesDto);

    @Operation(
        summary = "Deletes a stop place by ID",
        description = """
        Deletes a mono-modal StopPlace by its ID.
        Returns a job representing the asynchronous deletion process.
        """,
        parameters = {
            @Parameter(
                name = "stopPlaceId",
                description = "NeTEx StopPlace ID (e.g. NSR:StopPlace:1234)",
                required = true,
                example = "NSR:StopPlace:1234"
            ),
        },
        responses = {
            @ApiResponse(
                responseCode = "202",
                description = "Deletion job submitted",
                content = @Content(
                    schema = @Schema(implementation = StopPlaceJobDto.class)
                )
            ),
        }
    )
    Response deleteStopPlace(@PathParam("stopPlaceId") String stopPlaceId);
}
