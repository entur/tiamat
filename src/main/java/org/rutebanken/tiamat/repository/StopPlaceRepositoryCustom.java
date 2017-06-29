package org.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Envelope;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
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

    Page<StopPlace> findStopPlacesWithin(double xMin, double yMin, double xMax, double yMax, String ignoreStopPlaceId, Instant pointInTime, Pageable pageable);

    String findNearbyStopPlace(Envelope envelope, String name, StopTypeEnumeration stopTypeEnumeration);

    String findNearbyStopPlace(Envelope envelope, String name);

    List<String> findNearbyStopPlace(Envelope envelope, StopTypeEnumeration stopTypeEnumeration);

    String findFirstByKeyValues(String key, Set<String> value);

    Set<String> findByKeyValues(String key, Set<String> values);

    Set<String> findByKeyValues(String key, Set<String> values, boolean exactMatch);

    List<String> searchByKeyValue(String key, String value);
    
    List<IdMappingDto> findKeyValueMappingsForQuay(int recordPosition, int recordsPerRoundTrip);

    List<IdMappingDto> findKeyValueMappingsForStop(int recordPosition, int recordsPerRoundTrip);

    List<String> findStopPlaceFromQuayOriginalId(String quayOriginalId);

    Iterator<StopPlace> scrollStopPlaces();

    Iterator<StopPlace> scrollStopPlaces(ExportParams exportParams);

    Page<StopPlace> findStopPlace(ExportParams exportParams);

    Page<StopPlace> findStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search);

    List<StopPlace> findAll(List<String> stopPlacesNetexIds);

    StopPlace findByQuay(Quay quay);
}
