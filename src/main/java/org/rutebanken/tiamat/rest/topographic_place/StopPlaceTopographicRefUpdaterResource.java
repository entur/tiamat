package org.rutebanken.tiamat.rest.topographic_place;

import com.google.common.collect.Lists;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.StopPlaceTopographicRefUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class StopPlaceTopographicRefUpdaterResource {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicRefUpdaterResource.class);

    private static final int WORKERS = 5;

    private static final int PARTITION_SIZE = 200;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private StopPlaceTopographicRefUpdater stopPlaceTopographicRefUpdater;

    @GET
    @Produces("application/json")
    public Set<String> updateTopographicReferenceForAllStops() throws IOException, InterruptedException {

        final Set<String> updatedStopPlaceIds = new ConcurrentHashSet<>();

        final AtomicInteger topographicPlacesCreated = new AtomicInteger();

        final ExecutorService executorService = Executors.newFixedThreadPool(WORKERS);

        List<Long> stopPlaceIds = stopPlaceRepository.getAllStopPlaceIds();

        List<List<Long>> partitionedStopPlaceList = Lists.partition(stopPlaceIds, PARTITION_SIZE);
        logger.info("Creating {} workers with partitions of {} stop place IDs", WORKERS, PARTITION_SIZE);
        for (List<Long> stopPlaceList : partitionedStopPlaceList) {
            executorService.execute(() -> stopPlaceTopographicRefUpdater.update(stopPlaceList, topographicPlacesCreated, updatedStopPlaceIds));
        }

        executorService.shutdown();
        logger.info("Awaiting termination");
        executorService.awaitTermination(40, TimeUnit.SECONDS);

        logger.info("Returning list of updated stop place IDs {}. Topographic places created: {}", updatedStopPlaceIds, topographicPlacesCreated);
        return updatedStopPlaceIds;
    }

}
