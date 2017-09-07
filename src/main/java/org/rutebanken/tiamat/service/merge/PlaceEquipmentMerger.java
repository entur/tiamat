package org.rutebanken.tiamat.service.merge;

import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceEquipmentMerger {

    @Autowired
    private VersionCreator versionCreator;

    public PlaceEquipment mergePlaceEquipments(PlaceEquipment fromPlaceEquipments, PlaceEquipment toPlaceEquipments) {
        if (fromPlaceEquipments != null) {
            if (toPlaceEquipments == null) {
                toPlaceEquipments = new PlaceEquipment();
            }
            List<InstalledEquipment_VersionStructure> fromInstalledEquipment = fromPlaceEquipments.getInstalledEquipment();
            List<InstalledEquipment_VersionStructure> toInstalledEquipment = toPlaceEquipments.getInstalledEquipment();
            if (fromInstalledEquipment != null) {
                fromInstalledEquipment.forEach(eq -> {
                    toInstalledEquipment.add(
                            versionCreator.createCopy(eq, InstalledEquipment_VersionStructure.class)
                    );
                });
            }
        }
        return toPlaceEquipments;
    }
}
