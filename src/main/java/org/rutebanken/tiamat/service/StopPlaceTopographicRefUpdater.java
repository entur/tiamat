package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class StopPlaceTopographicRefUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTopographicRefUpdater.class);

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;

    public boolean update(StopPlace stopPlace) {
        if (stopPlace.getTopographicPlace() == null) {
            logger.info("Stop Place does not have reference to topographic place: {}", stopPlace);

            countyAndMunicipalityLookupService.populateTopographicPlaceRelation(stopPlace);
            stopPlaceRepository.save(stopPlace);
            return true;
        }
        return false;
    }


}
