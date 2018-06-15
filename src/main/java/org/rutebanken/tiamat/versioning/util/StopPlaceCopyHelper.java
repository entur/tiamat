/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.versioning.util;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.stopplace.ChildFromParentResolver;
import org.rutebanken.tiamat.versioning.CopiedEntity;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StopPlaceCopyHelper {

    @Autowired
    private ChildFromParentResolver childFromParentResolver;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private VersionCreator versionCreator;

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
            parentCopy = versionCreator.createCopy(parentStopPlace.get(), StopPlace.class);
            sourceCopy = childFromParentResolver.resolveChildFromParent(parentCopy, sourceStopPlace.getNetexId(), sourceStopPlace.getVersion());
            return new CopiedEntity<>(sourceStopPlace, sourceCopy, parentStopPlace.get(), parentCopy);

        } else {
            sourceCopy = versionCreator.createCopy(sourceStopPlace, StopPlace.class);
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
