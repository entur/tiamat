package org.rutebanken.tiamat.writer;

import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.rutebanken.tiamat.lock.MutateLock;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.validation.StopPlaceMutationValidator;
import org.rutebanken.tiamat.writer.mapper.CreateStopPlaceMapper;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.TimeZone;

@Service
public class StopPlaceWriter {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceWriter.class);
    private final StopPlaceMutationValidator stopPlaceMutationValidator;
    private final StopPlaceVersionedSaverService stopPlaceVersionedSaverService;
    private final StopPlaceTerminator stopPlaceTerminator;
    private final StopPlaceRepository stopPlaceRepository;

    private final MutateLock mutateLock;
    private final VersionCreator versionCreator;
    private final NetexMapper netexMapper;
    private final CreateStopPlaceMapper createStopPlaceMapper;
    private final SubmittedStopPlaceUpdater submittedStopPlaceUpdater;

    public StopPlaceWriter(
            StopPlaceMutationValidator stopPlaceMutationValidator,
            StopPlaceVersionedSaverService stopPlaceVersionedSaverService,
            StopPlaceTerminator stopPlaceTerminator,
            StopPlaceRepository stopPlaceRepository,
            MutateLock mutateLock,
            VersionCreator versionCreator,
            NetexMapper netexMapper,
            CreateStopPlaceMapper createStopPlaceMapper,
            SubmittedStopPlaceUpdater submittedStopPlaceUpdater
    ) {
        this.stopPlaceMutationValidator = stopPlaceMutationValidator;
        this.stopPlaceVersionedSaverService = stopPlaceVersionedSaverService;
        this.stopPlaceTerminator = stopPlaceTerminator;
        this.stopPlaceRepository = stopPlaceRepository;
        this.mutateLock = mutateLock;
        this.versionCreator = versionCreator;
        this.netexMapper = netexMapper;
        this.createStopPlaceMapper = createStopPlaceMapper;
        this.submittedStopPlaceUpdater = submittedStopPlaceUpdater;
    }


    /**
     * The NetexMappingContext must be established on the thread that performs the mapping.
     * This service runs on a worker thread (async executor today, a message subscriber later),
     * not on the thread that accepted the request, so the context cannot be inherited from the
     * caller. Set unconditionally: worker threads are pooled and may hold a context from a
     * previous task.
     */
    private void establishNetexMappingContext() {
        NetexMappingContextThreadLocal.updateMappingContext(
                new SiteFrame().withFrameDefaults(
                        new VersionFrameDefaultsStructure().withDefaultLocale(
                                new LocaleStructure().withTimeZone(TimeZone.getDefault().getID())
                        )
                )
        );
    }

    @Transactional
    public StopPlace createStopPlace(org.rutebanken.netex.model.StopPlace newStopPlace) {
        establishNetexMappingContext();
        var tiamatStopPlace = netexMapper.mapToTiamatModel(newStopPlace);
        var cleanStopPlace = createStopPlaceMapper.createCopy(tiamatStopPlace, StopPlace.class);

        stopPlaceMutationValidator.validateStopPlaceMutation(cleanStopPlace);
        return mutateLock.executeInLock(() -> stopPlaceVersionedSaverService.saveNewVersion(cleanStopPlace));
    }

    @Transactional
    public StopPlace updateStopPlace(org.rutebanken.netex.model.StopPlace newStopPlace) {
        establishNetexMappingContext();
        long expectedVersion = requireSubmittedVersion(newStopPlace);
        return mutateLock.executeInLock(() -> {
            // The validator reads the current version inside the lock, so the comparison uses a
            // fresh read and not a version that the caller supplied.
            //
            // This check narrows the window, but it does not close it. The lock sits inside the
            // transaction, so the previous writer releases the lock before it commits. A writer
            // that waits on the lock can still read the superseded version. The unique constraint
            // on (netex_id, version) then refuses the second row, so the database keeps the first
            // write. But the caller gets a constraint message instead of the stale-version
            // reason. See #458.
            var existingStopPlace = stopPlaceMutationValidator.validateStopPlaceUpdate(
                    newStopPlace.getId(),
                    false,
                    expectedVersion
            );
            var tiamatStop = netexMapper.mapToTiamatModel(newStopPlace);

            var updatedStopPlace = versionCreator.createCopy(existingStopPlace, StopPlace.class);
            submittedStopPlaceUpdater.update(updatedStopPlace, tiamatStop);

            stopPlaceMutationValidator.validateStopPlaceMutation(updatedStopPlace);

            return stopPlaceVersionedSaverService.saveNewVersion(
                    existingStopPlace,
                    updatedStopPlace,
                    Set.of() // currently only mono-modal stops are supported
            );
        });
    }

    /**
     * The version attribute states which version the caller edited. Without it the writer cannot
     * find a stale update.
     * <p>
     * The attribute is required, not optional. An optional precondition lets every caller that
     * omits it overwrite a concurrent edit without a warning. That is the problem that this
     * requirement closes.
     * <p>
     * A create ignores the attribute, because a new stop place has no earlier version.
     */
    private static long requireSubmittedVersion(org.rutebanken.netex.model.StopPlace submitted) {
        String version = submitted.getVersion();
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException(
                    "An update must carry the version attribute of the stop place it edits.");
        }
        try {
            return Long.parseLong(version.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "The version attribute must be a number, but was '" + version + "'.");
        }
    }

    /**
     * @return the terminated stop place, so that the job can report the version that the
     *         termination produced.
     */
    @Transactional
    public StopPlace deleteStopPlace(String stopPlaceId) {
        // already uses mutateLock
        return stopPlaceTerminator.terminateStopPlace(
                stopPlaceId,
                Instant.now(),
                "Deleted via write API",
                ModificationEnumeration.DELETE
        );
    }
}
