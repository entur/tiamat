package org.rutebanken.tiamat.service;


import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class CountyAndMunicipalityLookupService {

    private static final Logger logger = LoggerFactory.getLogger(CountyAndMunicipalityLookupService.class);

    private final Supplier<List<Pair<String, Polygon>>> topographicPlaces = Suppliers.memoizeWithExpiration(getTopographicPlaceSupplier(), 10, TimeUnit.HOURS);

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
    }

    public Optional<TopographicPlace> findTopographicPlace(Point point, TopographicPlaceTypeEnumeration topographicPlaceType) {
        return topographicPlaces.get()
                .stream()
                .filter(pair -> point.within(pair.getSecond()))
                .map(pair -> topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(pair.getFirst()))
                .filter(topographicPlace -> topographicPlace.getTopographicPlaceType().equals(topographicPlaceType))
                .findAny();
    }

    public Optional<TopographicPlace> findCountyMatchingReferences(List<String> countyReferences, Point point) {
        return topographicPlaces.get()
                .stream()
                .filter(pair -> point.within(pair.getSecond()))
                .map(pair -> topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(pair.getFirst()))
                .filter(topographicPlace -> topographicPlace.getTopographicPlaceType().equals(TopographicPlaceTypeEnumeration.COUNTY))
                .filter(topographicPlace -> countyReferences.contains(topographicPlace.getNetexId()))
                .findAny();
    }

    private Supplier<List<Pair<String, Polygon>>> getTopographicPlaceSupplier() {
        return new Supplier<List<Pair<String, Polygon>>>() {
            @Override
            public List<Pair<String, Polygon>> get() {
                logger.info("Fetching topographic places from repository");
                return topographicPlaceRepository.findAllMaxVersion()
                        .stream()
                        .map(topographicPlace -> Pair.of(topographicPlace.getNetexId(), topographicPlace.getPolygon()))
                        .collect(toList());
            }
        };
    }
}
