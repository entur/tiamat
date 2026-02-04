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

package org.rutebanken.tiamat.service;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.general.ResettableMemoizer;
import org.rutebanken.tiamat.model.Site_VersionStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class TopographicPlaceLookupService {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceLookupService.class);

    private static final List<TopographicPlaceTypeEnumeration> ADMIN_LEVEL_ORDER = Arrays.asList(TopographicPlaceTypeEnumeration.MUNICIPALITY, TopographicPlaceTypeEnumeration.COUNTY, TopographicPlaceTypeEnumeration.COUNTRY);

    private final ResettableMemoizer<List<ImmutableTriple<String, TopographicPlaceTypeEnumeration, Geometry>>> memoizedTopographicPlaces = new ResettableMemoizer<>(getTopographicPlaceSupplier());

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    public boolean populateTopographicPlaceRelation(Site_VersionStructure siteVersionStructure) {

        if (!siteVersionStructure.hasCoordinates()) {
            return false;
        }

        Optional<TopographicPlace> topographicPlace = findTopographicPlace(siteVersionStructure.getCentroid());

        if (topographicPlace.isPresent()) {
            logger.trace("Found topographic place {} for site {}", siteVersionStructure.getTopographicPlace(), siteVersionStructure);
            TopographicPlace topographicPlaceMatch = topographicPlace.get();

            if(siteVersionStructure.getTopographicPlace() != null) {

                if(siteVersionStructure.getTopographicPlace().getNetexId().equals(topographicPlaceMatch.getNetexId())
                        && siteVersionStructure.getTopographicPlace().getVersion() == topographicPlaceMatch.getVersion()) {
                    logger.trace("Version and id is the same for {}-{} not doing update", topographicPlaceMatch.getNetexId(), topographicPlaceMatch.getVersion());
                    return false;
                }
                logger.debug("Changed topographic place from {}-{} to {}-{} for {}-{}",
                        siteVersionStructure.getTopographicPlace().getNetexId(),
                        siteVersionStructure.getTopographicPlace().getVersion(),
                        topographicPlaceMatch.getNetexId(),
                        topographicPlaceMatch.getVersion(),
                        siteVersionStructure.getNetexId(),
                        siteVersionStructure.getVersion());
            } else {
                logger.debug("Set topographic place to {}-{} for {}-{}",
                        topographicPlaceMatch.getNetexId(),
                        topographicPlaceMatch.getVersion(),
                        siteVersionStructure.getNetexId(),
                        siteVersionStructure.getVersion());
            }



            siteVersionStructure.setTopographicPlace(topographicPlaceMatch);

            return true;
        } else {
            logger.warn("Could not find topographic places from site's point: {}", siteVersionStructure);
            return false;
        }
    }

    public Optional<TopographicPlace> findTopographicPlace(Point point) {
        return memoizedTopographicPlaces.get()
                .stream()
                .filter(triple -> point.coveredBy(triple.getRight()))
                .peek(triple -> logger.debug("Found matching topographic place {} for point {}", triple.getLeft(), point))
                .map(triple -> {
                    TopographicPlace topographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(triple.getLeft());
                    if(topographicPlace == null) {
                        logger.warn("Cannot find topographic place from ID: {}", triple.getLeft());
                    }
                    return topographicPlace;
                })
                .filter(Objects::nonNull)
                .findAny();
    }

    public Optional<TopographicPlace> findTopographicPlaceByReference(List<String> topographicPlaceReferences, Point point) {
        return memoizedTopographicPlaces.get()
                .stream()
                .filter(triple -> topographicPlaceReferences.contains(triple.getLeft()))
                .filter(triple -> point.coveredBy(triple.getRight()))
                .map(triple -> topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(triple.getLeft()))
                .peek(topographicPlace -> logger.debug("Found topographic place match: {}", topographicPlace.getNetexId()))
                .findAny();
    }

    public void reset() {
        memoizedTopographicPlaces.reset();
    }

    private Supplier<List<ImmutableTriple<String, TopographicPlaceTypeEnumeration, Geometry>>> getTopographicPlaceSupplier() {
        return () -> {

            TopographicPlaceSearch topographicPlaceSearch = TopographicPlaceSearch.newTopographicPlaceSearchBuilder()
                    .versionValidity(ExportParams.VersionValidity.CURRENT_FUTURE)
                    .build();

            logger.info("Fetching topographic places from repository");
            List<ImmutableTriple<String, TopographicPlaceTypeEnumeration, Geometry>> topographicPlaces = topographicPlaceRepository.findTopographicPlace(topographicPlaceSearch)
                    .stream()
                    .filter(topographicPlace -> getZoneGeometry(topographicPlace) != null)
                    .filter(topographicPlace -> ADMIN_LEVEL_ORDER.contains(topographicPlace.getTopographicPlaceType()))
                    .sorted(new TopographicPlaceByAdminLevelComparator())
                    .map(topographicPlace -> ImmutableTriple.of(topographicPlace.getNetexId(), topographicPlace.getTopographicPlaceType(), getZoneGeometry(topographicPlace)))
                    .collect(toList());
            logger.info("Fetched {} topographic places from repository", topographicPlaces.size());
            return topographicPlaces;
        };
    }

    /**
     * Returns the geometry to use for spatial lookups.
     * Prefers multiSurface if present, otherwise falls back to polygon.
     * JTS coveredBy() works on both Polygon and MultiPolygon.
     */
    private Geometry getZoneGeometry(TopographicPlace topographicPlace) {
        if (topographicPlace.getMultiSurface() != null) {
            return topographicPlace.getMultiSurface();
        }
        return topographicPlace.getPolygon();
    }

    private static class TopographicPlaceByAdminLevelComparator implements Comparator<TopographicPlace> {
        @Override
        public int compare(TopographicPlace tp1, TopographicPlace tp2) {
            return ADMIN_LEVEL_ORDER.indexOf(tp1.getTopographicPlaceType()) - ADMIN_LEVEL_ORDER.indexOf(tp2.getTopographicPlaceType());
        }
    }

}
