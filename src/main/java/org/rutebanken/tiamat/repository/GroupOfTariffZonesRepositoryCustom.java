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

import org.rutebanken.tiamat.exporter.params.GroupOfTariffZonesSearch;
import org.rutebanken.tiamat.model.GroupOfTariffZones;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public interface GroupOfTariffZonesRepositoryCustom {

    List<GroupOfTariffZones> findGroupOfTariffZones(GroupOfTariffZonesSearch search);

    Iterator<GroupOfTariffZones> scrollGroupOfTariffZones();

    Iterator<GroupOfTariffZones> scrollGroupOfTariffZones(Set<Long> StopPlaceIds);


}

