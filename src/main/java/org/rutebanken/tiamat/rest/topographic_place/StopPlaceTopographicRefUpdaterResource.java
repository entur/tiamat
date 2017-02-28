package org.rutebanken.tiamat.rest.topographic_place;

import com.google.common.collect.Lists;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Look up topographic place references for already persisted stops.
 */
@Transactional
@Component
@Path("/topopgraphic_place_updater")
public class StopPlaceTopographicRefUpdaterResource {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicRefUpdaterResource.class);

    private static final int WORKERS = 5;

    private static final int PARTITION_SIZE = 200;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    @GET
    @Produces("application/json")
    public Set<String> updateTopographicReferenceForAllStops() throws IOException, InterruptedException {

        final Set<String> updatedStopPlaceIds = new ConcurrentHashSet<>();

        final AtomicInteger topographicPlacesCreated = new AtomicInteger();

        final ExecutorService executorService = Executors.newFixedThreadPool(WORKERS);

        List<Long> stopPlaceIds = stopPlaceRepository.getAllStopPlaceIds();

        List<List<Long>> partitionedStopPlaceList = Lists.partition(stopPlaceIds, PARTITION_SIZE);
        logger.info("Creating {} workers with partitions of {} stop place IDs", WORKERS, PARTITION_SIZE);
        for(List<Long> stopPlaceList : partitionedStopPlaceList) {
            executorService.execute(new TopographicPlaceUpdaterWorker(stopPlaceList, updatedStopPlaceIds, topographicPlacesCreated));
        }

        executorService.shutdown();
        logger.info("Awaiting termination");
        executorService.awaitTermination(40, TimeUnit.SECONDS);

        logger.info("Returning list of updated stop place IDs {}. Topographic places created: {}", updatedStopPlaceIds, topographicPlacesCreated);
        return updatedStopPlaceIds;
    }

    class TopographicPlaceUpdaterWorker implements Runnable {

        private final List<Long> stopPlaceIds;
        private final AtomicInteger topographicPlacesCreated;
        private final Set<String> updatedStopPlaceIds;

        public TopographicPlaceUpdaterWorker(List<Long> stopPlaceIds, Set<String> updatedStopPlaceIds, AtomicInteger topographicPlacesCreated) {
            this.stopPlaceIds = stopPlaceIds;
            this.topographicPlacesCreated = topographicPlacesCreated;
            this.updatedStopPlaceIds = updatedStopPlaceIds;
        }

        @Override
        public void run() {
            try {
                Iterator<StopPlace> iterator = stopPlaceRepository.scrollStopPlaces(stopPlaceIds);

                while (iterator.hasNext()) {
                    StopPlace stopPlace = iterator.next();

                    if (stopPlace.getTopographicPlace() == null) {
                        logger.info("Stop Place does not have reference to topographic place: {}", stopPlace);
                        try {
                            countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCreated);
                            stopPlaceRepository.save(stopPlace);
                            updatedStopPlaceIds.add(NetexIdMapper.getNetexId(stopPlace));
                        } catch (IOException e) {
                            logger.info("Issue looking up county and municipality for stop {}", stopPlace, e);
                        }
                    }
                }
            } catch (InterruptedException e) {
                logger.info("Interrupted getting stop place from queue. Done.");
                Thread.currentThread().interrupt();
            }
        }
    }
}
