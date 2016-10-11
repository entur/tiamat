package org.rutebanken.tiamat.gtfs;


import org.rutebanken.tiamat.repository.QuayRepository;
import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.rutebanken.tiamat.model.Quay;

@Component
public class GtfsStopEntityHandler implements EntityHandler {
    private static final Logger logger = LoggerFactory.getLogger(GtfsStopEntityHandler.class);

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GtfsStopToIfoptQuayMapper stopToQuayMapper;

    public void handleEntity(Object bean) {
        if (bean instanceof Stop) {
            Stop gtfsStop = (Stop) bean;
            logger.trace("Handle quay {} with id {}", gtfsStop.getName(), gtfsStop.getId());
            Quay quay = stopToQuayMapper.mapToQuay(gtfsStop);

            try {
                quayRepository.save(quay);
            } catch (DataIntegrityViolationException e) {

                logger.warn("Error saving quay with name {}", quay.getName(), e);
            }
        }
    }
}
