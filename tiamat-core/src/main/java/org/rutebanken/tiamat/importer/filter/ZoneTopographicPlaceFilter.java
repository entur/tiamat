/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.importer.filter;

import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ZoneTopographicPlaceFilter {

    private static final Logger logger = LoggerFactory.getLogger(ZoneTopographicPlaceFilter.class);

    @Autowired
    private TopographicPlaceLookupService topographicPlaceLookupService;


    public <T extends Zone_VersionStructure> List<T> filterByTopographicPlaceMatch(List<String> topographicPlaceReferences, List<T> zones) {
        return filterByTopographicPlaceMatch(topographicPlaceReferences, zones, false);
    }

    /**
     * Filter zones that does not belong to the given list of topographic place references
     *
     * @param topographicPlaceReferences NetexIDs of topographic places
     * @param zones to filter
     * @param negate negates the filter. Only stop places that is outside the given topographic places will be returned.
     * @return filtered list
     */
    public <T extends Zone_VersionStructure> List<T> filterByTopographicPlaceMatch(List<String> topographicPlaceReferences, List<T> zones, boolean negate) {

        if(topographicPlaceReferences == null || topographicPlaceReferences.isEmpty()) {
            logger.info("Cannot filter zones with empty topographic references: {}. Returning all zones.", topographicPlaceReferences);
            return zones;
        }

        if(zones == null || zones.isEmpty()) {
            logger.info("There are no zones to filter.");
            return zones;
        }

        return zones.parallelStream()
                .filter(zone -> {
                    if(zone.getCentroid() == null) {
                        logger.warn("Zone does not have centroid: {}", zone);
                        return false;
                    }
                    return true;
                })
                .filter(zone -> {
                    Optional<TopographicPlace> topographicPlace = topographicPlaceLookupService.findTopographicPlaceByReference(topographicPlaceReferences, zone.getCentroid());
                    if(topographicPlace.isPresent()) {
                        logger.debug("Found matching topographic place {} for zone {}. Negate: {}", topographicPlace.get().getNetexId(), zone, negate);
                        return negate ? false : true;
                    } else if(negate){
                        logger.debug("Keeping {}. Negate: {}", zone, negate);
                        return true;
                    } else {
                        logger.debug("Filtering out {}. Topographic references: {}. Negate: {}", zone, topographicPlaceReferences, negate);
                        return false;
                    }
                })
                .collect(toList());
    }
}
