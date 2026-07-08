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
 * Result of FareZone import operation.
 * Contains the imported FareZones and their netexIds for cleanup tracking.
 */
public class FareZoneImportResult {

    private final List<FareZone> importedFareZones;
    private final Set<String> importedNetexIds;

    public FareZoneImportResult(List<FareZone> importedFareZones, Set<String> importedNetexIds) {
        this.importedFareZones = importedFareZones;
        this.importedNetexIds = importedNetexIds;
    }

    public List<FareZone> getImportedFareZones() {
        return importedFareZones;
    }

    public Set<String> getImportedNetexIds() {
        return importedNetexIds;
    }
}
