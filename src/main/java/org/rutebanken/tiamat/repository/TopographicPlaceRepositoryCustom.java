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

import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface TopographicPlaceRepositoryCustom extends DataManagedObjectStructureRepository<TopographicPlace> {

    List<TopographicPlace> findTopographicPlace(TopographicPlaceSearch topographicPlaceSearch);

    List<TopographicPlace> findByNetexIdOrNameAndTypeMaxVersion(String name, TopographicPlaceTypeEnumeration topographicPlaceType);

    Iterator<TopographicPlace> scrollTopographicPlaces(Set<Long> stopPlaceDbIds);

    Iterator<TopographicPlace> scrollTopographicPlaces();

    List<TopographicPlace> getTopographicPlacesFromStopPlaceIds(Set<Long> stopPlaceDbIds);

    /**
     * Batch loading method for DataLoader - efficiently loads topographic places by netexId and version combinations
     * @param netexIdToVersions Map of netexId to set of versions for each topographic place to load
     * @return Map structured as netexId -> version -> TopographicPlace for efficient lookup
     */
    Map<String, Map<Long, TopographicPlace>> findByNetexIdsAndVersions(Map<String, Set<Long>> netexIdToVersions);
}
