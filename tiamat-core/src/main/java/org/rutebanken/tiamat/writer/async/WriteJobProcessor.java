package org.rutebanken.tiamat.writer.async;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.writer.JobService;
import org.rutebanken.tiamat.writer.StopPlaceWriter;
import org.rutebanken.tiamat.writer.xml.StopPlacesPayloadUnmarshaller;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Performs a write and completes its job, in a single transaction.
 * <p>
 * Separate from {@link DefaultWriteJobHandler} because the transaction boundary has to be a
 * proxied call, and because the failure has to be recorded <em>outside</em> it: once an exception
 * escapes an inner transactional method, Spring marks the transaction rollback only, so recording
 * the failure inside would be discarded at commit.
 * <p>
 * Nothing here catches. Failing loudly is what rolls the write back, which is what makes a job
 * that did not finish mean that nothing was written.
 */
@Component
public class WriteJobProcessor {

    private final JobService jobService;
    private final StopPlaceWriter domainService;
    private final StopPlacesPayloadUnmarshaller payloadUnmarshaller;

    public WriteJobProcessor(
            JobService jobService,
            StopPlaceWriter domainService,
            StopPlacesPayloadUnmarshaller payloadUnmarshaller
    ) {
        this.jobService = jobService;
        this.domainService = domainService;
        this.payloadUnmarshaller = payloadUnmarshaller;
    }

    /**
     * The write and the job completion commit together. If the claim was lost while the write was
     * running, {@link JobService#succeed} throws and the write is rolled back with it.
     */
    @Transactional
    public void process(WriteJobMessage message) {
        switch (message.operation()) {
            case CREATE -> create(message);
            case UPDATE -> update(message);
            case DELETE -> delete(message);
        }
    }

    private void create(WriteJobMessage message) {
        var dto = payloadUnmarshaller.unmarshal(message.payload());
        var stopPlaces = dto.getStopPlaces();
        requireMonomodal(classify(stopPlaces), "creation");

        var newStopPlace = stopPlaces.getFirst();
        var savedStopPlace = domainService.createStopPlace(newStopPlace);
        jobService.succeed(
                message.jobId(),
                List.of(new StopPlaceIdMapping(newStopPlace.getId(), savedStopPlace.getNetexId()))
        );
    }

    private void update(WriteJobMessage message) {
        var dto = payloadUnmarshaller.unmarshal(message.payload());
        var stopPlaces = dto.getStopPlaces();
        requireMonomodal(classify(stopPlaces), "updates");

        domainService.updateStopPlace(stopPlaces.getFirst());
        jobService.succeed(message.jobId(), null);
    }

    private void delete(WriteJobMessage message) {
        domainService.deleteStopPlace(message.payloadAsString());
        jobService.succeed(message.jobId(), null);
    }

    private static void requireMonomodal(StopPlaceStructure structure, String operation) {
        if (structure == StopPlaceStructure.MULTIMODAL) {
            throw new IllegalArgumentException(
                    "Multimodal stop place " + operation + " not currently supported in this endpoint."
            );
        }
        if (structure == StopPlaceStructure.INVALID) {
            throw new IllegalArgumentException("Invalid stop place structure.");
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
