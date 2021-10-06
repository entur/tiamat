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

import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.FareZoneSearch;
import org.rutebanken.tiamat.model.FareZone;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface FareZoneRepositoryCustom extends DataManagedObjectStructureRepository<FareZone> {

    List<FareZone> findFareZones(FareZoneSearch search);

    List<FareZone> getFareZonesFromStopPlaceIds(Set<Long> stopPlaceIds);

    Iterator<FareZone> scrollFareZones(Set<Long> stopPlaceDbIds);

    Iterator<FareZone> scrollFareZones(ExportParams exportParams);

    Optional<FareZone> findValidFareZone(String netexId);

    int countResult(Set<Long> stopPlaceIds);

    public List<FareZone> findAllValidFareZones();

    int updateStopPlaceTariffZoneRef();
}
