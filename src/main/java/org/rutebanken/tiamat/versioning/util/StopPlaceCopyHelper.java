package org.rutebanken.tiamat.versioning.util;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.stopplace.ChildFromParentResolver;
import org.rutebanken.tiamat.versioning.CopiedEntity;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StopPlaceCopyHelper {

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private ChildFromParentResolver childFromParentResolver;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    /**
     * Create copy of stop place, and if exists the parent stop place as well
     */
    public CopiedEntity<StopPlace> createCopies(StopPlace sourceStopPlace) {

        if(sourceStopPlace.getNetexId() == null) {
            // Brand new stop place
            return new CopiedEntity<>(null, sourceStopPlace, null, null);
        }

        final Optional<StopPlace> parentStopPlace = resolveParent(sourceStopPlace);
        StopPlace parentCopy = null;
        StopPlace sourceCopy;
        if(parentStopPlace.isPresent()) {
            parentCopy = stopPlaceVersionedSaverService.createCopy(parentStopPlace.get(), StopPlace.class);
            sourceCopy = childFromParentResolver.resolveChildFromParent(parentCopy, sourceStopPlace.getNetexId(), sourceStopPlace.getVersion());
            return new CopiedEntity<>(sourceStopPlace, sourceCopy, parentStopPlace.get(), parentCopy);

        } else {
            sourceCopy = stopPlaceVersionedSaverService.createCopy(sourceStopPlace, StopPlace.class);
            return new CopiedEntity<>(sourceStopPlace, sourceCopy, null, null);
        }
    }

    public Optional<StopPlace> resolveParent(StopPlace stopPlace) {

        if(hasParent(stopPlace)) {
            StopPlace parent = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getParentSiteRef().getRef(),
                    Long.parseLong(stopPlace.getParentSiteRef().getVersion()));

            return Optional.of(parent);
        }
        return Optional.empty();
    }

    private boolean hasParent(StopPlace stopPlace) {
        return stopPlace.getParentSiteRef() != null;
    }

}
