package org.rutebanken.tiamat.rest.write.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.PathParam;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;

@Tag(name = "Jobs", description = "Manage asynchronous stop place write jobs.")
interface JobController {
    @Operation(
        summary = "Get status of a stop place write job",
        description = """
        Returns the current status of a stop place write job.

        The status is one of four values. PROCESSING means that the job is not complete, and you
        must poll again. FINISHED means that the write is committed. FAILED means that the write
        did not occur. TIMED_OUT means that the job did not complete in time, and that nothing was
        written, so you can submit the request again.

        The status also says which of the two outcome objects to read. A FINISHED job gives
        `result`. A FAILED or TIMED_OUT job gives `failure`. A PROCESSING job gives neither.

        `result.stopPlaces` holds one entry for every stop place that the job wrote. Each entry
        gives the ID of the stop place and the version that this write produced. Send that version
        with your next update of the same stop place. A create also gives `submittedId`, which is
        the ID that you sent, so that you can match the entry to your request.

        `failure.reasonCode` says why the write did not happen, and `failure.message` describes it
        in English. When `reasonCode` is STALE_VERSION, `failure.currentVersion` gives the version
        that the stop place is at now. Read the stop place again, apply your change to that
        version, and submit it again.

        A job is only visible to the principal that submitted it. The API reports a job from
        another principal as not found, and not as forbidden, so that you cannot probe job ids.
        """,
        parameters = {
            @Parameter(
                name = "jobId",
                description = "ID of the job to query",
                required = true,
                example = "88991"
            ),
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Job status returned successfully",
                content = @Content(
                    schema = @Schema(implementation = StopPlaceJobDto.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "No such job, or the job was submitted by a different principal",
                content = @Content(
                    examples = @ExampleObject(
                        value = """
                                                    {
                                                       "errors": [
                                                         {
                                                           "message": "Job with ID 123 not found"
                                                         }
                                                       ]
                                                     }
                        """
                    )
                )
            ),
        }
    )
    StopPlaceJobDto getJobStatus(@PathParam("jobId") Long jobId);
}
