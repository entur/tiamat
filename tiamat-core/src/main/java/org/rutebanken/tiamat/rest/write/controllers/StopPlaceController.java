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
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;

@Tag(
    name = "Stop Places write API",
    description = """
    Write mono-modal StopPlace entities asynchronously.

    The system sets the ValidBetween attribute, and ignores the one in the NeTEx XML that you
    submit. On an update, the Version attribute states which version you edited, and the API
    refuses the write if that version is no longer current. On a create, the API ignores the
    Version attribute. Timestamps in the NeTEx XML that the API returns always use the timezone
    of the system, without timezone information.

    The API does not read a write request before it accepts the request. The API answers a
    syntactically correct request with 202 and a job id. The API then reads and validates the
    payload asynchronously. Malformed XML and an unsupported element become a FAILED job with
    a reason. To find the outcome of a write request, poll the job endpoint.

    Only one check is synchronous: can this caller use the write API. If that check fails, the
    API answers 403. The per-entity authorization limits a caller to some stop place types and
    administrative zones. The API applies it during processing, so an unauthorized write
    request also becomes a FAILED job.
    """
)
interface StopPlaceController {

    @Operation(
        summary = "Creates a stop place from a NeTEx XML representation",
        description = """
        The API accepts a NeTEx StopPlace XML document as the request body.
        The document must contain only one mono-modal stop place.
        The system replaces the NeTEx ID that you submit with a generated ID.
        The API returns a job for the asynchronous create operation.
        If the operation is successful, the job result includes the generated NeTEx ID.
        The API reports malformed XML and unsupported elements on the job.
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
                description = "The API accepted the create job",
                content = @Content(
                    schema = @Schema(implementation = StopPlaceJobDto.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "The API cannot read the request payload"),
            @ApiResponse(responseCode = "403", description = "The caller does not have the role for the write API"),
            @ApiResponse(responseCode = "413", description = "The payload is larger than the maximum size"),
            @ApiResponse(responseCode = "503", description = "The job queue is full"),
        }
    )
    Response createStopPlace(InputStream body);

    @Operation(
        summary = "Updates a stop place from a NeTEx XML representation",
        description = """
        The API accepts a NeTEx StopPlace XML document as the request body.
        The document must contain only one mono-modal stop place.
        The API returns a job for the asynchronous update operation.

        The document that you submit replaces the stop place, thus you must send the full
        entity. The API removes the quays, the AccessibilityAssessment and the placeEquipments
        that are not in your document. If a quay has an unknown NeTEx ID, the job fails. If a
        quay has no ID, the API adds a new quay.

        The Version attribute must state the version that you edited. If another client changed
        the stop place after you read it, the job fails and the reason gives the current
        version. Read the stop place again, apply your change to it, and submit it again.

        The API reports malformed XML and unsupported elements on the job.
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
                description = "The API accepted the update job",
                content = @Content(
                    schema = @Schema(implementation = StopPlaceJobDto.class)
                )
            ),
            @ApiResponse(responseCode = "400", description = "The API cannot read the request payload"),
            @ApiResponse(responseCode = "403", description = "The caller does not have the role for the write API"),
            @ApiResponse(responseCode = "413", description = "The payload is larger than the maximum size"),
            @ApiResponse(responseCode = "503", description = "The job queue is full"),
        }
    )
    Response updateStopPlace(InputStream body);

    @Operation(
        summary = "Deletes a stop place by NeTEx ID",
        description = """
        The API deletes a stop place by its NeTEx ID.
        The API returns a job for the asynchronous delete operation.

        The API does not find the stop place before it accepts the job. Thus the API reports an
        unknown NeTEx ID as a FAILED job with a reason, and not as a 404. The API reports these
        two conditions in the same way: a stop place that is already deleted, and a child of a
        multimodal stop place. You cannot delete a child stop place on its own.
        """,
        parameters = {
            @Parameter(
                name = "stopPlaceId",
                description = "NeTEx StopPlace ID, for example NSR:StopPlace:1234",
                required = true,
                example = "NSR:StopPlace:1234"
            ),
        },
        responses = {
            @ApiResponse(
                responseCode = "202",
                description = "The API accepted the delete job",
                content = @Content(
                    schema = @Schema(implementation = StopPlaceJobDto.class)
                )
            ),
            @ApiResponse(responseCode = "403", description = "The caller does not have the role for the write API"),
            @ApiResponse(responseCode = "503", description = "The job queue is full"),
        }
    )
    Response deleteStopPlace(@PathParam("stopPlaceId") String stopPlaceId);
}
