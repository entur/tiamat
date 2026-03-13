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
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

@Tag(
    name = "Stop Places write API",
    description = "Write mono-modal StopPlace entities asynchronously."
)
interface StopPlaceController {
    @Operation(
        summary = "Gets a stop place by ID",
        description = """
        Gets a mono-modal StopPlace by its ID.
        """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "The stop place ",
                content = @Content(
                    schema = @Schema(implementation = StopPlace.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Stop place by NeTEx ID not found"
            ),
        }
    )
    Response getStopPlace(String stopPlaceId);

    @Operation(
        summary = "Creates a stop place from a NeTEx XML representation",
        description = """
        Accepts a StopPlacesDto containing a NeTEx StopPlace XML.
        The NeTEx ID submitted will be overwritten by the system-generated ID for the created stop place.
        Only a single mono-modal stop place is allowed.
        Returns a job representing the asynchronous creation process.
        On successful creation, the job result will include the generated NeTEx ID of the created stop place.
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
            @ApiResponse(responseCode = "400", description = "Malformed input"),
            @ApiResponse(responseCode = "503", description = "Job queue full"),
        }
    )
    Response createStopPlace(StopPlacesDto stopPlacesDto);

    @Operation(
        summary = "Updates a stop place from a NeTEx XML representation",
        description = """
        Accepts a StopPlacesDto containing a NeTEx StopPlace XML.
        Only a single mono-modal stop place is allowed.
        Returns a job representing the asynchronous creation process.

        The version must be incremented by 1 for updates.
        """,
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/xml",
                examples = {
                    @ExampleObject(
                        name = "Update StopPlace Example",
                        value = """
                                                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                                                  <StopPlace id="MES:StopPlace:1" version="2">
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
                description = "Update job submitted",
                content = @Content(
                    schema = @Schema(implementation = StopPlaceJobDto.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "Malformed input"),
            @ApiResponse(responseCode = "503", description = "Job queue full"),
        }
    )
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
            @ApiResponse(responseCode = "503", description = "Job queue full"),
        }
    )
    Response deleteStopPlace(@PathParam("stopPlaceId") String stopPlaceId);
}
