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

import org.rutebanken.tiamat.dtoassembling.dto.IdMappingDto;
import org.rutebanken.tiamat.dtoassembling.dto.JbvCodeMappingDto;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.Value;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface QuayRepositoryCustom extends DataManagedObjectStructureRepository<Quay> {

    List<IdMappingDto> findKeyValueMappingsForQuay(Instant validFrom, Instant validTo, int recordPosition, int recordsPerRoundTrip);

    Set<String> findUniqueQuayIds(Instant validFrom, Instant validTo);

    List<JbvCodeMappingDto> findJbvCodeMappingsForQuay();

    /**
     * Batch loading method for DataLoader - efficiently loads quays by stop place IDs
     * @param stopPlaceIds Set of stop place IDs to load quays for
     * @return Map of stop place ID to list of quays
     */
    Map<Long, List<Quay>> findQuaysByStopPlaceIds(Set<Long> stopPlaceIds);

    /**
     * Batch loading method for DataLoader - efficiently loads key-value pairs by quay IDs
     * @param quayIds Set of quay IDs to load key-values for
     * @return Map of quay ID to map of key-value pairs
     */
    Map<Long, Map<String, Value>> findKeyValuesByIds(Set<Long> quayIds);
}
