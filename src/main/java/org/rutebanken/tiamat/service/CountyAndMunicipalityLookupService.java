package org.rutebanken.tiamat.service;


import com.google.common.util.concurrent.Striped;
import com.vividsolutions.jts.geom.Point;
import org.geotools.console.Option;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.pelias.PeliasReverseLookupClient;
import org.rutebanken.tiamat.pelias.model.Properties;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CountyAndMunicipalityLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CountyAndMunicipalityLookupService.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    public void populateCountyAndMunicipality(Site_VersionStructure siteVersionStructure) throws IOException, InterruptedException {

        if (!siteVersionStructure.hasCoordinates()) {
            return;
        }

        List<TopographicPlace> topographicPlaces = topographicPlaceRepository.findByPoint(siteVersionStructure.getCentroid());
        if(topographicPlaces == null || topographicPlaces.isEmpty()) {
            logger.warn("Could not find topographic places from site's point: {}", siteVersionStructure.getCentroid());
            return;
        }

        Optional<TopographicPlace> topographicPlaceWithParent = topographicPlaces.stream()
                .filter(topographicPlace -> topographicPlace.getParentTopographicPlaceRef() == null)
                .findAny();

        if(topographicPlaceWithParent.isPresent()) {
            siteVersionStructure.setTopographicPlace(topographicPlaceWithParent.get());
            logger.debug("Found topographic place {} for site {}", siteVersionStructure.getTopographicPlace(), siteVersionStructure);
        } else {
            logger.warn("Could not find topographic place with parent for site {}", siteVersionStructure);
        }

        Optional<TopographicPlace> topographicPlaceWithoutParent = topographicPlaces.stream()
                .filter(topographicPlace -> topographicPlace.getParentTopographicPlaceRef() != null)
                .findAny();

        if(topographicPlaceWithoutParent.isPresent()) {
            siteVersionStructure.setTopographicPlace(topographicPlaceWithoutParent.get());
            logger.warn("Found topographic place without parent: {} for site {}", siteVersionStructure.getTopographicPlace(), siteVersionStructure);
        } else {
            logger.warn("Could not find topographic place without parent for site {}", siteVersionStructure);
        }




    }
}
