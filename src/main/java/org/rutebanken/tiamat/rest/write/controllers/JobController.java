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

@Tag(name = "Jobs", description = "Manage asynchronous StopPlace jobs.")
interface JobController {
    @Operation(
        summary = "Get status of a StopPlace job",
        description = """
        Returns the current status of a StopPlace job.
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
                description = "Job not found",
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
