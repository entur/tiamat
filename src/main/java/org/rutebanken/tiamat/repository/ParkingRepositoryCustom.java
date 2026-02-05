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
import org.rutebanken.tiamat.exporter.params.ParkingSearch;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface ParkingRepositoryCustom extends DataManagedObjectStructureRepository<Parking>  {

    String findFirstByKeyValues(String key, Set<String> value);

    Iterator<Parking> scrollParkings(Set<Long> stopPlaceIds);

    int countResult(ParkingSearch parkingSearch);

    int countResult(Set<Long> stopPlaceIds);

    String findNearbyParking(Envelope boundingBox, String value, ParkingTypeEnumeration parkingType);


    Iterator<Parking> scrollParkings(ParkingSearch parkingSearch);

    /**
     * Find parkings that belong to StopPlace
     * @param netexStopPlaceId
     * @return list of parkings referencing to stopPlace
     */
    List<String> findByStopPlaceNetexId(String netexStopPlaceId);

    /**
     * Find parking entities that belong to StopPlace (optimized version that returns entities directly)
     * @param netexStopPlaceId
     * @return list of parking entities referencing to stopPlace
     */
    List<Parking> findParkingEntitiesByStopPlaceNetexId(String netexStopPlaceId);
}

