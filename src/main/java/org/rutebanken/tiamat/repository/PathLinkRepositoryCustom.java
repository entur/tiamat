package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.PathLink;

import java.util.List;
import java.util.Set;

public interface PathLinkRepositoryCustom {

    Long findByKeyValue(String key, Set<String> values);

    /**
     * Find pathlinks that have pathlink end referencing to quays of stop place
     * @param stopPlaceId
     * @return list of path links referencing to quays, which belong to stop place.
     */
    List<String> findByStopPlaceNetexId(String netexStopPlaceId);

    List<PathLink> findByStopPlaceIds(Set<Long> stopPlaceIds);
}
