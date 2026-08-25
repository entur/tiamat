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

import java.io.InputStream;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;

@Tag(
    name = "Stop Places write API",
    description = """
    Write mono-modal StopPlace entities asynchronously.

    Version and ValidBetween attributes in the submitted NeTEx XML are ignored, and will be managed by the system.
    Timestamps in the returned NeTEx XML will always use the systems timezone, without any timezone information.

    Write requests are not parsed before they are accepted. A syntactically valid request is
    answered with 202 and a job id, and the payload is then parsed and validated asynchronously.
    Rejections that a synchronous API would report as 400, such as malformed XML or an
    unsupported element, are therefore reported as a FAILED job with a reason. Poll the job
    endpoint to determine the outcome of a write.

    Only the coarse check that the caller may use the write API at all is applied synchronously,
    and it answers 403. The per entity authorization, which restricts a caller to certain stop
    place types and administrative zones, runs during processing and is reported as a FAILED job.
    An unauthorized write is therefore answered with 202 and fails afterwards.
    """
)
interface StopPlaceController {

    @Operation(
        summary = "Creates a stop place from a NeTEx XML representation",
        description = """
        Accepts a NeTEx StopPlace XML document as the request body.
        The NeTEx ID submitted will be overwritten by the system-generated ID for the created stop place.
        Only a single mono-modal stop place is allowed.
        Returns a job representing the asynchronous creation process.
        On successful creation, the job result will include the generated NeTEx ID of the created stop place.
        Malformed XML and unsupported elements are reported on the job, not as a 400.
        """,
        requestBody = @RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/xml",
                examples = {
                    @ExampleObject(
                        name = "Create StopPlace example",
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
            @ApiResponse(responseCode = "400", description = "Request payload could not be read"),
            @ApiResponse(responseCode = "403", description = "Missing the role required to use the write API"),
            @ApiResponse(responseCode = "413", description = "Payload exceeds the maximum supported size"),
            @ApiResponse(responseCode = "503", description = "Job queue full"),
        }
    )
    Response createStopPlace(InputStream body);

    @Operation(
        summary = "Updates a stop place from a NeTEx XML representation",
        description = """
        Accepts a NeTEx StopPlace XML document as the request body.
        Only a single mono-modal stop place is allowed.
        Returns a job representing the asynchronous update process.

        The submitted document replaces the stop place: the entire entity must be sent. Quays,
        AccessibilityAssessment and placeEquipments that are absent from the submitted document
        are removed from the stop place. Submitting a quay with an unknown NeTEx ID fails the
        job; a quay without an ID is added as a new quay.

        Malformed XML and unsupported elements are reported on the job, not as a 400.
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
            @ApiResponse(responseCode = "400", description = "Request payload could not be read"),
            @ApiResponse(responseCode = "403", description = "Missing the role required to use the write API"),
            @ApiResponse(responseCode = "413", description = "Payload exceeds the maximum supported size"),
            @ApiResponse(responseCode = "503", description = "Job queue full"),
        }
    )
    Response updateStopPlace(InputStream body);

    @Operation(
        summary = "Deletes a stop place by NeTEx ID",
        description = """
        Deletes a stop place by its NeTEx ID.
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
            @ApiResponse(responseCode = "404", description = "Stop place by NeTEx ID not found"),
            @ApiResponse(responseCode = "403", description = "Missing the role required to use the write API"),
            @ApiResponse(responseCode = "503", description = "Job queue full"),
        }
    )
    Response deleteStopPlace(@PathParam("stopPlaceId") String stopPlaceId);
}
