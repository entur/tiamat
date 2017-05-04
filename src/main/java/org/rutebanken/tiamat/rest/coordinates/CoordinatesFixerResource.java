package org.rutebanken.tiamat.rest.coordinates;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Transactional
@Component
@Produces("text/plain")
@Path("/coordinate-fixer")
public class CoordinatesFixerResource {

    private static final Logger logger = LoggerFactory.getLogger(CoordinatesFixerResource.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private TopographicPlaceLookupService countyAndMunicipalityLookupService;


    @POST
    @Consumes("text/plain")
    @Produces("application/json")
    public Set<String> fixCoordinates(InputStream inputStream) throws IOException {

        logger.info("Received request to fix coordinates");
        Set<String> updatedStopPlaceIds = new HashSet<>();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        AtomicInteger topographicPlacesCreated = new AtomicInteger();

        while(bufferedReader.ready()) {

            String line = bufferedReader.readLine();
            logger.info("Got line: {}", line);

            String[] columns = line.split(";");
            String prefix = columns[0];
            String name = columns[1];
            String coordinates = columns[2];

            logger.info("Parsed prefix: {}, name: {}, coordinates: {}", prefix, name, coordinates);

            StopPlaceSearch stopPlaceSearch = new StopPlaceSearch.Builder().setQuery(name).build();
            Page<StopPlace> stopPlaces = stopPlaceRepository.findStopPlace(stopPlaceSearch);

            logger.info("Found {} stop places from name {}", stopPlaces.getTotalElements(), name);

            for(StopPlace stopPlace : stopPlaces.getContent()) {

                logger.info("Inspecting stop place: {}", stopPlace);

                boolean containsQuaysWithoutCoordinates =
                        stopPlace.getQuays() == null ? false : stopPlace.getQuays().stream()
                                .anyMatch(quay -> quay.getCentroid() == null);

                if(stopPlace.getCentroid() == null || containsQuaysWithoutCoordinates) {

                    logger.info("Found stop place or quay without centroid... {}", stopPlace);

                    Set<String> originalIds = stopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY);
                    logger.info("Found original Ids for stop: {}", originalIds);

                    for(String originalId : originalIds) {
                        logger.info("The stop place with ID {} contains original ID: {}", stopPlace.getNetexId(), originalId);

                        if(originalId.startsWith(prefix)) {
                            logger.info("The original ID {} starts with the prefix {}", originalId, prefix);

                            String[] coordinatesSplitted = coordinates.split(",");
                            String latitude = coordinatesSplitted[0];
                            String longitude = coordinatesSplitted[1];

                            logger.info("Parsed: Latitude: {}, Longitude: {}", latitude, longitude);

                            Point point = geometryFactory.createPoint(new Coordinate(Double.valueOf(longitude), Double.valueOf(latitude)));


                            stopPlace.setCentroid(point);

                            if(stopPlace.getQuays() != null) {
                                stopPlace.getQuays().forEach(quay -> {
                                    quay.setCentroid(point);
                                    logger.info("Setting point {} on quay {}", point, quay);
                                    quayRepository.save(quay);
                                });
                            }

                            countyAndMunicipalityLookupService.populateTopographicPlaceRelation(stopPlace);

                            logger.info("Saving stop place {}", stopPlace);
                            updatedStopPlaceIds.add(stopPlace.getNetexId());
                            stopPlaceRepository.save(stopPlace);

                        } else {
                            logger.info("The stop's original ID {} does not match prefix {}", originalId, prefix);
                        }
                    }

                } else {
                    logger.info("Stop place has already centroid, ignoring: {}", stopPlace);
                }
            }

       }

       logger.info("Returning list of updated stop place IDs {}. Topographic places created: {}", updatedStopPlaceIds, topographicPlacesCreated);
       return updatedStopPlaceIds;
    }

}
