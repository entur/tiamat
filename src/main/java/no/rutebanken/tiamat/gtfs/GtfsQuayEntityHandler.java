package no.rutebanken.tiamat.gtfs;


import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import uk.org.netex.netex.Quay;
import uk.org.netex.netex.StopPlace;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class GtfsQuayEntityHandler implements EntityHandler {
    private static final Logger logger = LoggerFactory.getLogger(GtfsQuayEntityHandler.class);

    private static final Executor executor = Executors.newFixedThreadPool(2);

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GtfsIfoptMapper gtfsIfoptMapper;

    public void handleEntity(Object bean) {
        if (bean instanceof Stop) {
            Stop gtfsStop = (Stop) bean;
            logger.trace("Handle quay {} with id {}", gtfsStop.getName(), gtfsStop.getId());
            Quay quay = gtfsIfoptMapper.mapToQuay(gtfsStop);

            try {
                quayRepository.save(quay);
            } catch (DataIntegrityViolationException e) {

                logger.warn("Error saving quay with name {}", quay.getName(), e);
            }
        }
    }
}
