package org.rutebanken.tiamat.rest.write.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.rutebanken.tiamat.rest.write.JobService;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Produces(MediaType.APPLICATION_JSON)
@Path("write/jobs")
public class JobControllerImpl implements JobController {

    private final JobService jobService;

    @Autowired
    public JobControllerImpl(JobService jobService) {
        this.jobService = jobService;
    }

    @GET
    @Path("/{jobId}")
    public StopPlaceJobDto getJobStatus(@PathParam("jobId") Long jobId) {
        return jobService
            .getJob(jobId)
            .map(StopPlaceJobDto::from)
            .orElseThrow(() ->
                new NotFoundException("Job with ID " + jobId + " not found")
            );
    }
}
