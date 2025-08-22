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
import org.rutebanken.tiamat.exporter.params.TariffZoneSearch;
import org.rutebanken.tiamat.model.TariffZone;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


public interface TariffZoneRepositoryCustom extends DataManagedObjectStructureRepository<TariffZone> {

    List<TariffZone> findTariffZones(TariffZoneSearch search);

    List<TariffZone> getTariffZonesFromStopPlaceIds(Set<Long> stopPlaceIds);

    Iterator<TariffZone> scrollTariffZones(Set<Long> stopPlaceDbIds);

    Iterator<TariffZone> scrollTariffZones(ExportParams exportParams);

    Optional<TariffZone> findValidTariffZone(String netexId);

    List<TariffZone> findAllValidTariffZones();

    List<TariffZone> findValidTariffZones(List<String> netexIds);

    int updateStopPlaceTariffZoneRef();

    /**
     * Batch loading method for DataLoader - efficiently loads tariff zones by stop place IDs
     * @param stopPlaceIds Set of stop place IDs to load tariff zones for
     * @return Map of stop place ID to list of tariff zones
     */
    Map<Long, List<TariffZone>> findTariffZonesByStopPlaceIds(Set<Long> stopPlaceIds);
}
