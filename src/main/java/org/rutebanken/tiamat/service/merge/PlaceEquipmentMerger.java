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

package org.rutebanken.tiamat.service.merge;

import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceEquipmentMerger {

    @Autowired
    private VersionCreator versionCreator;

    @Autowired
    private VersionIncrementor versionIncrementor;

    public PlaceEquipment mergePlaceEquipments(PlaceEquipment fromPlaceEquipments, PlaceEquipment toPlaceEquipments) {
        if (fromPlaceEquipments != null) {
            if (toPlaceEquipments == null) {
                toPlaceEquipments = new PlaceEquipment();
            }
            List<InstalledEquipment_VersionStructure> fromInstalledEquipment = fromPlaceEquipments.getInstalledEquipment();
            List<InstalledEquipment_VersionStructure> toInstalledEquipment = toPlaceEquipments.getInstalledEquipment();
            if (fromInstalledEquipment != null) {
                fromInstalledEquipment.forEach(eq -> {
                    var newVersion = versionCreator.createCopy(eq, InstalledEquipment_VersionStructure.class);
                    versionIncrementor.incrementVersion(newVersion);
                    toInstalledEquipment.add(newVersion);
                });
            }
        }
        return toPlaceEquipments;
    }
}
