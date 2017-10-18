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
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Resolve and fetch child stop places from parent stop places from a list of stops
 */
@Service
public class ChildStopPlacesFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ChildStopPlacesFetcher.class);

    /**
     * Resolve children of parent stops.
     * Existing children are removed (as they will be added from parent stops).
     *
     * @param stopPlaceList with parent or monomodal stops. List should not already contain children
     * @return list of stops with children of parents resolved. Other monomodal stops are kept
     */
    public List<StopPlace> resolveChildren(List<StopPlace> stopPlaceList) {

        if (stopPlaceList == null || stopPlaceList.stream().noneMatch(sp -> sp != null)) {
            return stopPlaceList;
        }

        List<StopPlace> result = new ArrayList<>();

        stopPlaceList.forEach(stopPlace -> {
            if(stopPlace.isParentStopPlace()) {

                Set<StopPlace> children = stopPlace.getChildren();
                if(children.isEmpty()) {
                    logger.info("The parent stop place {}-{} does not have any children.", stopPlace.getNetexId(), stopPlace.getVersion());
                }
                result.add(stopPlace);
                logger.info("Adding {} children of parent stop {}-{}", children.size(), stopPlace.getNetexId(), stopPlace.getVersion());
                result.addAll(children);
            } else {
                if (stopPlace.getParentSiteRef() != null
                        && Strings.isNullOrEmpty(stopPlace.getParentSiteRef().getRef())
                        && stopPlace.getParentSiteRef().getVersion() != null) {
                    logger.warn("The list of stop places given to this method shall not contain child stops. " +
                            "The stop {}-{} has a reference to parent site {} and will not be added",
                            stopPlace.getNetexId(), stopPlace.getVersion(),
                            stopPlace.getParentSiteRef());
                } else {
                    result.add(stopPlace);
                }
            }
        });
        logger.info("Returning {} stops. Was {}", result.size(), stopPlaceList.size());
        return result;
    }

}
