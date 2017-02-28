package org.rutebanken.tiamat.rest.topographic_place;

import com.google.common.collect.Lists;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.StopPlaceTopographicRefUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Look up topographic place references for already persisted stops.
 */
@Component
@Path("/topopgraphic_place_updater")
@Transactional
public class StopPlaceTopographicRefUpdaterResource {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicRefUpdaterResource.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceTopographicRefUpdater stopPlaceTopographicRefUpdater;

    @GET
    @Produces("application/json")
    public Set<String> updateTopographicReferenceForAllStops() throws IOException, InterruptedException {

        final Set<String> updatedStopPlaceIds = new ConcurrentHashSet<>();

        final AtomicInteger topographicPlacesCreated = new AtomicInteger();

        List<Long> stopPlaceIds = stopPlaceRepository.getAllStopPlaceIds();

        stopPlaceTopographicRefUpdater.update(stopPlaceIds, topographicPlacesCreated, updatedStopPlaceIds);

        logger.info("Returning list of updated stop place IDs {}. Topographic places created: {}", updatedStopPlaceIds, topographicPlacesCreated);
        return updatedStopPlaceIds;
    }

}
