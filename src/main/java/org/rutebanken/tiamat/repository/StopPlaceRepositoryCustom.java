package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Envelope;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.rutebanken.tiamat.model.StopPlace;

import java.util.List;


public interface StopPlaceRepositoryCustom {

    /**
     * Find stop place by id and load it with eager fetching on relations.
     * Used for testing and to be able to export a more complete graph.
     *
     * @param id the stop place id
     * @return the stop place with relations fetched.
     */
    StopPlace findStopPlaceDetailed(Long id);

    Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, Long ignoreStopPlaceId, Pageable pageable);

    Long findNearbyStopPlace(Envelope envelope, String name);

    Long findByKeyValue(String key, String value);

    Page<StopPlace> findStopPlace(String name, Long municipalityId, Long countyId, List<StopTypeEnumeration> stopPlaceTypes, Pageable pageable);
}
