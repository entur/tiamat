package org.rutebanken.tiamat.importer.filter;

import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.coordinates.CountyFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ZoneCountyFilterer {

    private static final Logger logger = LoggerFactory.getLogger(ZoneCountyFilterer.class);

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;
    
    /**
     * Filter zones that does not belong to the given list of county references
     *
     * @param countyReferences NetexIDs
     * @param zones to filter
     * @return filtered list
     */
    public List<? extends Zone_VersionStructure> filterByCountyMatch(List<String> countyReferences, List<? extends Zone_VersionStructure> zones) {


        if(countyReferences == null || countyReferences.isEmpty()) {
            logger.info("Cannot filter zones with empty county references: {}. Returning all zones.", countyReferences);
            return zones;
        }

        if(zones == null || zones.isEmpty()) {
            logger.info("There are no zones to filter.");
            return zones;
        }

        return zones.stream()
                .filter(zone -> {
                    if(zone.getCentroid() == null) {
                        logger.warn("Zone does not have centroid: {}", zone);
                        return false;
                    }
                    return true;
                })
                .filter(zone -> {

                    List<TopographicPlace> places = topographicPlaceRepository.findAllMaxVersion()
                            .stream()
                            .filter(topographicPlace -> topographicPlace.getTopographicPlaceType().equals(TopographicPlaceTypeEnumeration.COUNTY))
                            .filter(topographicPlace -> countyReferences.contains(topographicPlace.getNetexId()))
                            .filter(topographicPlace -> zone.getCentroid().within(topographicPlace.getPolygon()))
                            .peek(topographicPlace -> logger.info("Found matching topographic place {} for zone {}", topographicPlace.getNetexId(), zone))
                            .collect(toList());
                    return !places.isEmpty();
                })
                .collect(toList());
    }
}
