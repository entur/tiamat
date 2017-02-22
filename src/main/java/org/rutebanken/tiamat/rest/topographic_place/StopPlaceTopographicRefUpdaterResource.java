package org.rutebanken.tiamat.rest.topographic_place;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
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

    private static final int WORKERS = 10;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    @GET
    @Produces("application/json")
    public Set<String> updateTopographicReferenceForAllStops() throws IOException, InterruptedException {

        Set<String> updatedStopPlaceIds = new ConcurrentHashSet<>();

        BlockingQueue<StopPlace> queue = stopPlaceRepository.scrollStopPlaces();

        AtomicInteger topographicPlacesCreated = new AtomicInteger();

        ExecutorService executorService = Executors.newFixedThreadPool(WORKERS);

        for (int i = 0; i < WORKERS; i++) {
            executorService.execute(() -> {
                while (true) {
                    try {
                        StopPlace stopPlace = queue.poll(5, TimeUnit.SECONDS);

                        if (stopPlace == null) {
                            logger.info("Got null stop place from queue. Done.");
                            executorService.shutdownNow();
                            break;
                        }

                        if (stopPlace.getId() == StopPlaceRepositoryImpl.POISON_PILL.getId()) {
                            logger.info("Got poison pill. Done.");
                            executorService.shutdownNow();
                            break;
                        }

                        if (stopPlace.getTopographicPlace() == null) {
                            logger.info("Stop Place does not have reference to topographic place: {}", stopPlace);
                            try {
                                countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCreated);
                                updatedStopPlaceIds.add(NetexIdMapper.getNetexId(stopPlace));
                            } catch (IOException e) {
                                logger.info("Issue looking up county and municipality for stop {}", stopPlace, e);
                            }
                        }
                    } catch (InterruptedException e) {
                        logger.info("Interrupted getting stop place from queue. Done.");
                    }
                }
            });
        }

        executorService.shutdown();
        logger.info("Awaiting termination");
        executorService.awaitTermination(40, TimeUnit.SECONDS);

        logger.info("Returning list of updated stop place IDs {}. Topographic places created: {}", updatedStopPlaceIds, topographicPlacesCreated);
        return updatedStopPlaceIds;
    }


}
