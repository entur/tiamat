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

package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.FareZone;

import java.util.List;
import java.util.Set;

/**
 * Result of a FareZone import.
 *
 * <p>Two id sets, because replica cleanup and reference validation need different answers:
 * <ul>
 *   <li>{@code declaredNetexIds} - every zone the delivery carried, including one that failed to save.
 *       Used as the replica cleanup keep-set so a rejected zone's existing version is not pruned as an orphan.</li>
 *   <li>{@code savedNetexIds} - only zones actually persisted this round. Used to validate group-of-tariff-zones
 *       members, so a rejected new zone does not satisfy a member reference (a dangling ref); a rejected
 *       pre-existing zone still resolves from the database.</li>
 * </ul>
 */
public class FareZoneImportResult {

    private final List<FareZone> importedFareZones;
    private final Set<String> savedNetexIds;
    private final Set<String> declaredNetexIds;

    public FareZoneImportResult(List<FareZone> importedFareZones, Set<String> savedNetexIds, Set<String> declaredNetexIds) {
        this.importedFareZones = importedFareZones;
        this.savedNetexIds = savedNetexIds;
        this.declaredNetexIds = declaredNetexIds;
    }

    public List<FareZone> getImportedFareZones() {
        return importedFareZones;
    }

    public Set<String> getSavedNetexIds() {
        return savedNetexIds;
    }

    public Set<String> getDeclaredNetexIds() {
        return declaredNetexIds;
    }
}
