package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableAsync
public class InProcessStopPlaceProcessor implements StopPlaceAsyncProcessor {

    private static final Logger logger = LoggerFactory.getLogger(
        InProcessStopPlaceProcessor.class
    );
    private final JobService jobService;
    private final StopPlaceDomainService domainService;
    private final NetexMapper netexMapper;

    public InProcessStopPlaceProcessor(
        JobService jobService,
        StopPlaceDomainService domainService,
        NetexMapper netexMapper
    ) {
        this.jobService = jobService;
        this.domainService = domainService;
        this.netexMapper = netexMapper;
    }

    @Async("stopPlaceExecutor")
    public void processCreateStopPlace(Long jobId, StopPlacesDto dto) {
        try {
            var validatedStopPlace = validateAndGetSingleStopPlace(dto);
            var savedStopPlace = domainService.createStopPlace(validatedStopPlace.stopPlace);
            jobService.succeed(
                jobId,
                List.of(
                    new StopPlaceIdMapping(
                        validatedStopPlace.originalId,
                        savedStopPlace.getNetexId()
                    )
                )
            );
        } catch (Exception e) {
            logger.error("Error creating stop place", e);
            jobService.fail(jobId, e);
        }
    }

    @Async("stopPlaceExecutor")
    public void processUpdateStopPlace(Long jobId, StopPlacesDto dto) {
        try {
            var validatedStopPlace = validateAndGetSingleStopPlace(dto);
            domainService.updateStopPlace(validatedStopPlace.stopPlace);
            jobService.succeed(jobId, null);
        } catch (Exception e) {
            logger.error("Error updating stop place", e);
            jobService.fail(jobId, e);
        }
    }

    @Async("stopPlaceExecutor")
    public void processDeleteStopPlace(Long jobId, String stopPlaceId) {
        try {
            domainService.deleteStopPlace(stopPlaceId);
            jobService.succeed(jobId, null);
        } catch (Exception e) {
            logger.error("Error deleting stop place", e);
            jobService.fail(jobId, e);
        }
    }

    private ValidatedStopPlace validateAndGetSingleStopPlace(StopPlacesDto dto) {
        if (dto.getStopPlaces().size() != 1) {
            throw new IllegalArgumentException(
                "Exactly one stop place must be provided"
            );
        }

        var dtoStopPlace = dto.getStopPlaces().getFirst();
        var stopPlace = netexMapper.mapToTiamatModel(dtoStopPlace);

        if (
            stopPlace.isParentStopPlace() || !stopPlace.getChildren().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Only mono-modal stop place allowed"
            );
        }

        return new ValidatedStopPlace(stopPlace, dtoStopPlace.getId());
    }

    private record ValidatedStopPlace(StopPlace stopPlace, String originalId) {}
}
