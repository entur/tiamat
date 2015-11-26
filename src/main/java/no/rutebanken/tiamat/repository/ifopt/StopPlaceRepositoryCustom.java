package no.rutebanken.tiamat.repository.ifopt;

import uk.org.netex.netex.StopPlace;


public interface StopPlaceRepositoryCustom {

    /**
     * Find stop place by id and load it with eager fetching on relations.
     * Used for testing and to be able to export a more complete graph.
     *
     * @param id the stop place id
     * @return the stop place with relations fetched.
     */
    StopPlace findStopPlaceDetailed(String id);
}
