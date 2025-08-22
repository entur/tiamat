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

package org.rutebanken.tiamat.repository;

import org.locationtech.jts.geom.Envelope;
import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.search.ChangedStopPlaceSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    List<IdMappingDto> findKeyValueMappingsForStop(Instant validFrom, Instant validTo, int recordPosition, int recordsPerRoundTrip);

    Set<String> findUniqueStopPlaceIds(Instant validFrom, Instant validTo);

    List<String> findStopPlaceFromQuayOriginalId(String quayOriginalId, Instant pointInTime);

    Iterator<StopPlace> scrollStopPlaces();

    Iterator<StopPlace> scrollStopPlaces(ExportParams exportParams);

    Set<String> getNetexIds(ExportParams exportParams);

    Set<Long> getDatabaseIds(ExportParams exportParams, boolean ignorePaging);

    Page<StopPlace> findStopPlace(ExportParams exportParams);

    Page<StopPlace> findStopPlacesWithEffectiveChangeInPeriod(ChangedStopPlaceSearch search);

    List<StopPlace> findAll(List<String> stopPlacesNetexIds);

    StopPlace findByQuay(Quay quay);

    List<JbvCodeMappingDto> findJbvCodeMappingsForStopPlace();

    Iterator<StopPlace> scrollStopPlaces(Set<Long> stopPlacePrimaryIds);

    Map<String, Set<String>> listStopPlaceIdsAndQuayIds(Instant validFrom, Instant validTo);

    int deleteStopPlaceTariffZoneRefs();

    /**
     * Batch load stop places by netexId and version combinations
     * @param netexIdToVersions map of netexId to set of versions to load
     * @return map of (netexId, version) to StopPlace
     */
    Map<String, Map<Long, StopPlace>> findByNetexIdsAndVersions(Map<String, Set<Long>> netexIdToVersions);

    /**
     * Efficiently finds latest version StopPlaces for the given netex IDs using a window function approach
     * @param netexIds List of netex IDs to find latest versions for
     * @return List of latest version StopPlace entities
     */
    List<StopPlace> findLatestVersionByNetexIds(List<String> netexIds);

    /**
     * Optimized method for report queries that eagerly fetches all required associations
     * to avoid N+1 query problems when loading large datasets with deep nesting.
     * This method uses JOIN FETCH to load all data in minimal queries.
     *
     * @param exportParams The search parameters
     * @param includeChildren Whether to fetch children for parent stop places
     * @param includeQuays Whether to fetch quays and their associations
     * @return Page of StopPlaces with eagerly loaded associations
     */
    Page<StopPlace> findStopPlacesForReport(ExportParams exportParams, boolean includeChildren, boolean includeQuays);
}
