package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Envelope;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public interface StopPlaceRepositoryCustom extends DataManagedObjectStructureRepository<StopPlace> {


    Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, String ignoreStopPlaceId, Pageable pageable);

    String findNearbyStopPlace(Envelope envelope, String name, StopTypeEnumeration stopTypeEnumeration);

    String findNearbyStopPlace(Envelope envelope, String name);

    List<String> findNearbyStopPlace(Envelope envelope, StopTypeEnumeration stopTypeEnumeration);

    String findByKeyValue(String key, Set<String> value);

    List<String> searchByKeyValue(String key, String value);
    
    List<IdMappingDto> findKeyValueMappingsForQuay(int recordPosition, int recordsPerRoundTrip);

    List<IdMappingDto> findKeyValueMappingsForStop(int recordPosition, int recordsPerRoundTrip);

    Iterator<StopPlace> scrollStopPlaces() throws InterruptedException;

    Iterator<StopPlace> scrollStopPlaces(List<String> stopPlaceNetexIds) throws InterruptedException;

    Page<StopPlace> findStopPlace(StopPlaceSearch stopPlaceSearch);

    Page<StopPlace> findStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search);

    List<StopPlace> findAll(List<String> stopPlacesNetexIds);

    StopPlace findByQuay(Quay quay);
}
