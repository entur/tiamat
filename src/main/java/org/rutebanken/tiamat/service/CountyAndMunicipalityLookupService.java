package org.rutebanken.tiamat.service;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CountyAndMunicipalityLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CountyAndMunicipalityLookupService.class);

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    public void populateCountyAndMunicipality(Site_VersionStructure siteVersionStructure) {

        if (!siteVersionStructure.hasCoordinates()) {
            return;
        }

        List<TopographicPlace> topographicPlaces = topographicPlaceRepository.findByPoint(siteVersionStructure.getCentroid());
        if (topographicPlaces == null || topographicPlaces.isEmpty()) {
            logger.warn("Could not find topographic places from site's point: {}", siteVersionStructure.getCentroid());
            return;
        }

        Optional<TopographicPlace> topographicPlaceWithParent = topographicPlaces.stream()
                .filter(topographicPlace -> topographicPlace.getParentTopographicPlaceRef() == null)
                .findAny();

        if (topographicPlaceWithParent.isPresent()) {
            siteVersionStructure.setTopographicPlace(topographicPlaceWithParent.get());
            logger.debug("Found topographic place {} for site {}", siteVersionStructure.getTopographicPlace(), siteVersionStructure);
        } else {
            logger.warn("Could not find topographic place with parent for site {}", siteVersionStructure);
        }

        Optional<TopographicPlace> topographicPlaceWithoutParent = topographicPlaces.stream()
                .filter(topographicPlace -> topographicPlace.getParentTopographicPlaceRef() != null)
                .findAny();

        if (topographicPlaceWithoutParent.isPresent()) {
            siteVersionStructure.setTopographicPlace(topographicPlaceWithoutParent.get());
            logger.warn("Found topographic place without parent: {} for site {}", siteVersionStructure.getTopographicPlace(), siteVersionStructure);
        } else {
            logger.warn("Could not find topographic place without parent for site {}", siteVersionStructure);
        }
    }
}
