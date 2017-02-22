package org.rutebanken.tiamat.rest.topographic_place;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.rutebanken.tiamat.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Look up topographic place references for already persisted stops.
 */
@Transactional
@Component
@Produces("text/plain")
@Path("/topographic-place-fixer")
public class TopographicPlaceLookup {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceLookup.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    @POST
    @Produces("application/json")
    public Set<String> fixCoordinates() throws IOException, InterruptedException {

        Set<String> updatedStopPlaceIds = new HashSet<>();

        BlockingQueue<StopPlace> queue = stopPlaceRepository.scrollStopPlaces();

        AtomicInteger topographicPlacesCreated = new AtomicInteger();

        while(true) {
            try {
                StopPlace stopPlace = queue.take();

                if(stopPlace.getId() == StopPlaceRepositoryImpl.POISON_PILL.getId()) {
                    logger.info("Got poison pill. Done.");
                    break;
                }

                if(stopPlace.getTopographicPlace() == null) {
                    logger.info("Stop Place does not have reference to topographic place: {}", stopPlace);
                    try {
                        countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCreated);
                        updatedStopPlaceIds.add(NetexIdMapper.getNetexId(stopPlace));
                    } catch (IOException e) {
                        logger.info("Issue looking up county and municipality for stop {}", stopPlace);
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Error getting stop place from queue");
            }
        }

       logger.info("Returning list of updated stop place IDs {}. Topographic places created: {}", updatedStopPlaceIds, topographicPlacesCreated);
       return updatedStopPlaceIds;
    }

}
