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

package org.rutebanken.tiamat.service.stopplace;

import com.google.common.base.Strings;
import org.hibernate.Session;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

/**
 * Resolve and fetch parent stop places from a list of stops
 */
@Service
public class ParentStopPlacesFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParentStopPlacesFetcher.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final EntityManager entityManager;

    public ParentStopPlacesFetcher(StopPlaceRepository stopPlaceRepository, EntityManager entityManager) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.entityManager = entityManager;
    }

    public List<StopPlace> resolveParents(List<StopPlace> stopPlaceList, boolean keepChilds) {

        Session session = entityManager.unwrap(Session.class);

        if (stopPlaceList == null || stopPlaceList.stream().noneMatch(sp -> sp != null)) {
            return stopPlaceList;
        }

        List<StopPlace> result = stopPlaceList.stream().filter(StopPlace::isParentStopPlace).collect(toList());

        List<StopPlace> nonParentStops = stopPlaceList.stream().filter(stopPlace -> !stopPlace.isParentStopPlace()).collect(toList());

        nonParentStops.forEach(nonParentStop -> {
            if (nonParentStop.getParentSiteRef() != null) {
                // Parent stop place refs should have version. If not, let it fail.
                StopPlace parent = stopPlaceRepository.findFirstByNetexIdAndVersion(nonParentStop.getParentSiteRef().getRef(),
                        Long.parseLong(nonParentStop.getParentSiteRef().getVersion()));
                if (parent != null) {
                    logger.info("Resolved parent: {} {} from child {}", parent.getNetexId(), parent.getName(), nonParentStop.getNetexId());

                    if(nonParentStop.getName() == null || Strings.isNullOrEmpty(nonParentStop.getName().getValue())) {
                        logger.info("Copying name from parent {} to child stop: {}", parent.getId(), parent.getName());
                        nonParentStop.setName(parent.getName());
                        session.setReadOnly(nonParentStop, true);
                    }

                    if (result.stream().noneMatch(stopPlace -> stopPlace.getNetexId() != null
                                                                       && (stopPlace.getNetexId().equals(parent.getNetexId()) && stopPlace.getVersion() == parent.getVersion()))) {
                        result.add(parent);
                    }
                    if (keepChilds) {
                        result.add(nonParentStop);
                    }
                } else {
                    logger.warn("Could not resolve parent from {}", nonParentStop.getParentSiteRef());
                }
            } else {
                result.add(nonParentStop);
            }
        });

        return result;
    }

}
