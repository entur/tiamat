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
 * Result of a FareZone import. {@code declaredNetexIds} includes zones that failed to save, so replica
 * cleanup does not prune their existing versions; {@code savedNetexIds} does not, so a rejected new zone
 * cannot satisfy a group member reference.
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
