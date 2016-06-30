package no.rutebanken.tiamat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import no.rutebanken.tiamat.model.StopPlace;


public interface StopPlaceRepositoryCustom {

    /**
     * Find stop place by id and load it with eager fetching on relations.
     * Used for testing and to be able to export a more complete graph.
     *
     * @param id the stop place id
     * @return the stop place with relations fetched.
     */
    StopPlace findStopPlaceDetailed(String id);

    Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, Pageable pageable);

    StopPlace findByKeyValue(String key, String value);

}
