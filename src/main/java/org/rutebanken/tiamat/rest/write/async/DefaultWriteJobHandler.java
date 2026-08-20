package org.rutebanken.tiamat.rest.write.async;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.rest.write.JobService;
import org.rutebanken.tiamat.rest.write.StopPlaceWriteDomainService;
import org.rutebanken.tiamat.rest.write.StopPlacesPayloadUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Processes a write job, whichever transport delivered it.
 * <p>
 * Everything here is transport agnostic on purpose. A transport implementation should be a thin
 * shell that hands the message over; keeping unmarshalling, structure validation, the domain call
 * and the job outcome in one place means a second transport cannot drift from the first, and in
 * particular cannot reproduce a subtly different version of the correctness or security handling.
 */
@Component
public class DefaultWriteJobHandler implements WriteJobHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultWriteJobHandler.class);

    private final JobService jobService;
    private final StopPlaceWriteDomainService domainService;
    private final StopPlacesPayloadUnmarshaller payloadUnmarshaller;

    public DefaultWriteJobHandler(
            JobService jobService,
            StopPlaceWriteDomainService domainService,
            StopPlacesPayloadUnmarshaller payloadUnmarshaller
    ) {
        this.jobService = jobService;
        this.domainService = domainService;
        this.payloadUnmarshaller = payloadUnmarshaller;
    }

    @Override
    public void handle(WriteJobMessage message) {
        if (!jobService.claim(message.jobId())) {
            // Already claimed by another delivery, or already terminal. Doing the work anyway
            // would duplicate it: a create mints a fresh NSR id on every run.
            logger.debug("Job {} could not be claimed, discarding delivery", message.jobId());
            return;
        }
        switch (message.operation()) {
            case CREATE -> handleCreate(message);
            case UPDATE -> handleUpdate(message);
            case DELETE -> handleDelete(message);
        }
    }

    private void handleCreate(WriteJobMessage message) {
        Long jobId = message.jobId();
        try {
            var dto = payloadUnmarshaller.unmarshal(message.payload());
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

    private void handleUpdate(WriteJobMessage message) {
        Long jobId = message.jobId();
        try {
            var dto = payloadUnmarshaller.unmarshal(message.payload());
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

    private void handleDelete(WriteJobMessage message) {
        Long jobId = message.jobId();
        try {
            domainService.deleteStopPlace(message.payloadAsString());
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
