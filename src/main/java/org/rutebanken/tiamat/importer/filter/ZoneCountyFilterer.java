package org.rutebanken.tiamat.importer.filter;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.service.CountyAndMunicipalityLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ZoneCountyFilterer {

    private static final Logger logger = LoggerFactory.getLogger(ZoneCountyFilterer.class);

    @Autowired
    private CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;


    public List<? extends Zone_VersionStructure> filterByCountyMatch(List<String> countyReferences, List<? extends Zone_VersionStructure> zones) {
        return filterByCountyMatch(countyReferences, zones, false);
    }

    /**
     * Filter zones that does not belong to the given list of county references
     *
     * @param countyReferences NetexIDs
     * @param zones to filter
     * @param negate negates the filter. Only stop places that is outside the given counties will be returned.
     * @return filtered list
     */
    public List<? extends Zone_VersionStructure> filterByCountyMatch(List<String> countyReferences, List<? extends Zone_VersionStructure> zones, boolean negate) {

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
                    Optional<TopographicPlace> topographicPlace = countyAndMunicipalityLookupService.findCountyMatchingReferences(countyReferences, zone.getCentroid());
                    if(topographicPlace.isPresent()) {
                        logger.debug("Found matching topographic place {} for zone {}. Negate: {}", topographicPlace.get().getNetexId(), zone, negate);
                        return negate ? false : true;
                    } else {
                        logger.info("No matching counties for {}. Negate: {}", zone, negate);
                        return negate ? true : false;
                    }
                })
                .collect(toList());
    }
}
