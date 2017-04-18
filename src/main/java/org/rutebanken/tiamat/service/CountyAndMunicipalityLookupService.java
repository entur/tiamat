package org.rutebanken.tiamat.service;


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Service
public class CountyAndMunicipalityLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CountyAndMunicipalityLookupService.class);

    private final Supplier<List<TopographicPlace>> topographicPlaces = Suppliers.memoizeWithExpiration(getTopographicPlaceSupplier(), 10, TimeUnit.HOURS);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    public void populateCountyAndMunicipality(Site_VersionStructure siteVersionStructure) {

        if (!siteVersionStructure.hasCoordinates()) {
            return;
        }

        Optional<TopographicPlace> topographicPlace = findTopographicPlace(siteVersionStructure.getCentroid(), TopographicPlaceTypeEnumeration.TOWN);

        if(topographicPlace.isPresent()) {
            logger.debug("Found topographic place {} for site {}", siteVersionStructure.getTopographicPlace(), siteVersionStructure);
            siteVersionStructure.setTopographicPlace(topographicPlace.get());
        } else {
            logger.warn("Could not find topographic places from site's point: {}", siteVersionStructure.getCentroid());
        }

        List<TopographicPlace> topographicPlaces = topographicPlaceRepository.findByPoint(siteVersionStructure.getCentroid());
        if (topographicPlaces == null || topographicPlaces.isEmpty()) {
            logger.warn("Could not find topographic places from site's point: {}", siteVersionStructure.getCentroid());
            return;
        }
    }

    public Optional<TopographicPlace> findTopographicPlace(Point point, TopographicPlaceTypeEnumeration topographicPlaceType) {
        return topographicPlaces.get()
                .stream()
                .filter(topographicPlace -> topographicPlace.getTopographicPlaceType().equals(topographicPlaceType))
                .filter(topographicPlace -> point.within(topographicPlace.getPolygon()))
                .findAny();
    }

    public Optional<TopographicPlace> findCountyMatchingReferences(List<String> countyReferences, Point point) {
        return topographicPlaces.get()
                .stream()
                .filter(topographicPlace -> topographicPlace.getTopographicPlaceType().equals(TopographicPlaceTypeEnumeration.COUNTY))
                .filter(topographicPlace -> countyReferences.contains(topographicPlace.getNetexId()))
                .filter(topographicPlace -> point.within(topographicPlace.getPolygon()))
                .findAny();
    }

    private Supplier<List<TopographicPlace>> getTopographicPlaceSupplier() {
        return new Supplier<List<TopographicPlace>>() {
            @Override
            public List<TopographicPlace> get() {
                logger.info("Fetching topographic places from repository");
                return topographicPlaceRepository.findAllMaxVersion()
                        .stream()
                        .filter(topographicPlace -> topographicPlace.getPolygon() != null)
                        .collect(toList());
            }
        };
    }
}
