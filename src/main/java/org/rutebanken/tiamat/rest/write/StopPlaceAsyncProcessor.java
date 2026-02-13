package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableAsync
public class StopPlaceAsyncProcessor {

    private static final Logger logger = LoggerFactory.getLogger(
        StopPlaceAsyncProcessor.class
    );
    private final JobService jobService;
    private final StopPlaceDomainService domainService;

    public StopPlaceAsyncProcessor(
        JobService jobService,
        StopPlaceDomainService domainService
    ) {
        this.jobService = jobService;
        this.domainService = domainService;
    }

    @Async("stopPlaceExecutor")
    public void processCreateStopPlace(Long jobId, StopPlace stopPlace) {
        try {
            var savedStopPlace = domainService.createStopPlace(stopPlace);
            jobService.succeed(jobId, List.of(savedStopPlace.getNetexId()));
        } catch (Exception e) {
            logger.error("Error creating stop place", e);
            jobService.fail(jobId, e.getMessage());
        }
    }

    @Async("stopPlaceExecutor")
    public void processUpdateStopPlace(Long jobId, StopPlace stopPlace) {
        try {
            domainService.updateStopPlace(stopPlace);
            jobService.succeed(jobId, List.of(stopPlace.getNetexId()));
        } catch (Exception e) {
            logger.error("Error updating stop place", e);
            jobService.fail(jobId, e.getMessage());
        }
    }

    @Async("stopPlaceExecutor")
    public void processDeleteStopPlace(Long jobId, String stopPlaceId) {
        try {
            domainService.deleteStopPlace(stopPlaceId);
            jobService.succeed(jobId, null);
        } catch (Exception e) {
            logger.error("Error deleting stop place", e);
            jobService.fail(jobId, e.getMessage());
        }
    }
}
