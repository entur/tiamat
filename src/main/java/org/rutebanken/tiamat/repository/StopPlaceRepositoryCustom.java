package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Envelope;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;


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

    Long findNearbyStopPlace(Envelope envelope, String name, StopTypeEnumeration stopTypeEnumeration);

    List<Long> findNearbyStopPlace(Envelope envelope, StopTypeEnumeration stopTypeEnumeration);

    Long findByKeyValue(String key, Set<String> value);

    List<Long> searchByKeyValue(String key, String value);
    
    List<IdMappingDto> findKeyValueMappingsForQuay(int recordPosition, int recordsPerRoundTrip);

    List<IdMappingDto> findKeyValueMappingsForStop(int recordPosition, int recordsPerRoundTrip);

    BlockingQueue<StopPlace> scrollStopPlaces() throws InterruptedException;

    Page<StopPlace> findStopPlace(StopPlaceSearch stopPlaceSearch);
}
