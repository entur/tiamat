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

package org.rutebanken.tiamat.exporter.async;

import com.google.common.base.Strings;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParentStopFetchingIterator implements Iterator<StopPlace> {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopFetchingIterator.class);

    private final Iterator<StopPlace> iterator;

    private final StopPlaceRepository stopPlaceRepository;


    private final Map<String, EmbeddableMultilingualString> parents = new HashMap<>();

    private StopPlace parent = null;

    public ParentStopFetchingIterator(Iterator<StopPlace> iterator, StopPlaceRepository stopPlaceRepository) {
        this.iterator = iterator;
        this.stopPlaceRepository = stopPlaceRepository;
    }

    @Override
    public boolean hasNext() {
        return parent != null || iterator.hasNext();
    }

    @Override
    public StopPlace next() {

        if (parent != null) {
            StopPlace next = parent;
            parent = null;
            return next;
        }


        StopPlace stopPlace = iterator.next();
        if (stopPlace.getParentSiteRef() != null) {
            String parentRefString = refString(stopPlace.getParentSiteRef());
            if (!parents.containsKey(parentRefString)) {
                parent = stopPlaceRepository.findFirstByNetexIdAndVersion(stopPlace.getParentSiteRef().getRef(), Long.parseLong(stopPlace.getParentSiteRef().getVersion()));
                logger.info("Fetched parent during iteration: {} - {}", parent.getNetexId(), parent.getVersion());
                parents.put(parentRefString, parent.getName());
            }
            copyNameFromParentIfMissing(parentRefString, parents.get(parentRefString), stopPlace);

        }

        return stopPlace;
    }

    public void copyNameFromParentIfMissing(String parentRefString, EmbeddableMultilingualString parentName, StopPlace childStopPlace) {
        if (childStopPlace.getName() == null || Strings.isNullOrEmpty(childStopPlace.getName().getValue())) {
            logger.info("Copying name: {} from parent {} to child stop: {}", parentName, parentRefString, childStopPlace.getNetexId());
            childStopPlace.setName(parentName);
        }
    }


    private String refString(SiteRefStructure siteRefStructure) {
        return siteRefStructure.getRef() + "-" + siteRefStructure.getVersion();
    }
}
