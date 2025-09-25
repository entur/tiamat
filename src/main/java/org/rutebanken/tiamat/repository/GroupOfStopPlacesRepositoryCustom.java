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

import org.rutebanken.tiamat.exporter.params.GroupOfStopPlacesSearch;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface GroupOfStopPlacesRepositoryCustom {

    List<GroupOfStopPlaces> findGroupOfStopPlaces(GroupOfStopPlacesSearch search);

    List<GroupOfStopPlaces> getGroupOfStopPlacesFromStopPlaceIds(Set<Long> stopPlaceIds);

    Iterator<GroupOfStopPlaces> scrollGroupOfStopPlaces();

    Iterator<GroupOfStopPlaces> scrollGroupOfStopPlaces(Set<Long> stopPlaceDbIds);

    /**
     * Batch loading method for DataLoader - efficiently loads groups by stop place IDs
     * Returns groups mapped by stop place ID for efficient DataLoader usage
     * @param stopPlaceIds Set of stop place IDs to load groups for
     * @return Map of stop place ID to list of groups containing that stop place
     */
    java.util.Map<Long, List<GroupOfStopPlaces>> findGroupsByStopPlaceIds(Set<Long> stopPlaceIds);
}

