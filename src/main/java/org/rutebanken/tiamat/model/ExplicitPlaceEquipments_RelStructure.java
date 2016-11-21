package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ExplicitPlaceEquipments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<InstalledEquipment_VersionStructure> otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment;

    public List<InstalledEquipment_VersionStructure> getOtherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment() {
        if (otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment == null) {
            otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment = new ArrayList<InstalledEquipment_VersionStructure>();
        }
        return this.otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment;
    }

}
