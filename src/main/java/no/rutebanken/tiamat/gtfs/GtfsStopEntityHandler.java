package no.rutebanken.tiamat.gtfs;


import no.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.StopPlace;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class GtfsStopEntityHandler implements EntityHandler {
    private static final Logger logger = LoggerFactory.getLogger(GtfsStopEntityHandler.class);

    private static final Executor executor = Executors.newFixedThreadPool(2);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private GtfsIfoptMapper gtfsIfoptMapper;

    @Autowired
    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    public void handleEntity(Object bean) {
        if (bean instanceof Stop) {
            Stop stop = (Stop) bean;
            logger.trace("Handle stop {} with id {}", stop.getName(), stop.getId());
            StopPlace stopPlace = gtfsIfoptMapper.map(stop);

            try {
                countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace);
                stopPlaceRepository.save(stopPlace);
            } catch (DataIntegrityViolationException | IOException e) {

                logger.warn("Error saving stop place with name {}", stopPlace.getName(), e);
            }
        }
    }
}
