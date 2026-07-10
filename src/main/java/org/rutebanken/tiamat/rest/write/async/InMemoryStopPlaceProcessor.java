package org.rutebanken.tiamat.rest.write.async;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.rest.write.JobService;
import org.rutebanken.tiamat.rest.write.StopPlaceWriteDomainService;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableAsync
@ConditionalOnProperty(name = "tiamat.write-api.in-memory-processor.enabled", havingValue = "true")
public class InMemoryStopPlaceProcessor implements StopPlaceAsyncProcessor {

    private static final Logger logger = LoggerFactory.getLogger(
        InMemoryStopPlaceProcessor.class
    );
    private final JobService jobService;
    private final StopPlaceWriteDomainService domainService;

    public InMemoryStopPlaceProcessor(
        JobService jobService,
        StopPlaceWriteDomainService domainService
    ) {
        this.jobService = jobService;
        this.domainService = domainService;
    }

    @Async("stopPlaceWriteExecutor")
    public void processCreateStopPlace(Long jobId, StopPlacesDto dto) {
        try {
            var structure = classify(dto.getStopPlaces());
            if (structure == StopPlaceStructure.MONOMODAL) {
                var newStopPlace = dto.getStopPlaces().getFirst();
                var savedStopPlace = domainService.createStopPlace(newStopPlace);
                jobService.succeed(
                        jobId,
                        List.of(
                                new StopPlaceIdMapping(
                                        newStopPlace.getId(),
                                        savedStopPlace.getNetexId()
                                )
                        )
                );
            } else if (structure == StopPlaceStructure.MULTIMODAL) {
                throw new IllegalArgumentException(
                        "Multimodal stop place creation not currently supported in this endpoint."
                );
            } else if (structure == StopPlaceStructure.INVALID) {
                throw new IllegalArgumentException(
                        "Invalid stop place structure."
                );
            }
        } catch (Exception e) {
            logger.error("Error creating stop place", e);
            jobService.fail(jobId, e);
        }
    }

    @Async("stopPlaceWriteExecutor")
    public void processUpdateStopPlace(Long jobId, StopPlacesDto dto) {
        try {
            var structure = classify(dto.getStopPlaces());
            if (structure == StopPlaceStructure.MONOMODAL) {
                domainService.updateStopPlace(dto.getStopPlaces().getFirst());
            } else if (structure == StopPlaceStructure.MULTIMODAL) {
                 throw new IllegalArgumentException(
                    "Multimodal stop place updates not currently supported in this endpoint."
                 );
            } else if (structure == StopPlaceStructure.INVALID) {
                throw new IllegalArgumentException(
                    "Invalid stop place structure."
                );
            }
            jobService.succeed(jobId, null);
        } catch (Exception e) {
            logger.error("Error updating stop place", e);
            jobService.fail(jobId, e);
        }
    }

    @Async("stopPlaceWriteExecutor")
    public void processDeleteStopPlace(Long jobId, String stopPlaceId) {
        try {
            domainService.deleteStopPlace(stopPlaceId);
            jobService.succeed(jobId, null);
        } catch (Exception e) {
            logger.error("Error deleting stop place", e);
            jobService.fail(jobId, e);
        }
    }

    public enum StopPlaceStructure {
        MULTIMODAL,
        MONOMODAL,
        INVALID
    }

    public StopPlaceStructure classify(List<StopPlace> stopPlaces) {
        if (stopPlaces == null || stopPlaces.isEmpty()) {
            return StopPlaceStructure.INVALID;
        }

        List<StopPlace> roots = stopPlaces.stream()
                .filter(sp -> sp.getParentSiteRef() == null)
                .toList();

        if (stopPlaces.size() == 1 && roots.size() == 1) {
            var monoModalStopPlace = stopPlaces.getFirst();
            if (monoModalStopPlace.getKeyList() != null &&
                    monoModalStopPlace.getKeyList().getKeyValue().stream()
                            .anyMatch(kv -> "IS_PARENT_STOP_PLACE".equals(kv.getKey()) &&
                                    "true".equalsIgnoreCase(kv.getValue()))) {
                return StopPlaceStructure.INVALID;
            }
            return StopPlaceStructure.MONOMODAL;
        }

        if (roots.size() == 1) {
            String parentId = roots.getFirst().getId();
            boolean allChildrenReferenceParent = stopPlaces.stream()
                    .filter(sp -> sp.getParentSiteRef() != null)
                    .allMatch(sp -> parentId.equals(sp.getParentSiteRef().getRef()));

            if (allChildrenReferenceParent) {
                return StopPlaceStructure.MULTIMODAL;
            }
        }

        return StopPlaceStructure.INVALID;
    }
}