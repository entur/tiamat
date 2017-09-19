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

package org.rutebanken.tiamat.rest.topographic_place;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTopographicRefUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Look up topographic place references for already persisted stops.
 */
@Component
@Path("/topographic_place_updater")
public class StopPlaceTopographicRefUpdaterResource {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicRefUpdaterResource.class);

    @Autowired
    private StopPlaceTopographicRefUpdater stopPlaceTopographicRefUpdater;
    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @GET
    @Produces("application/json")
    public Set<String> updateTopographicReferenceForAllStops() throws IOException, InterruptedException {

        final Set<String> updatedStopPlaceIds = new ConcurrentHashSet<>();
        Iterator<StopPlace> iterator = stopPlaceRepository.scrollStopPlaces();

        while (iterator.hasNext()) {
            StopPlace stopPlace = iterator.next();

            if (stopPlaceTopographicRefUpdater.update(stopPlace)) {
                updatedStopPlaceIds.add(stopPlace.getNetexId());
            }
        }
        logger.info("Returning list of updated stop place IDs {}.", updatedStopPlaceIds);
        return updatedStopPlaceIds;
    }

}
