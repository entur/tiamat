

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class ExplicitPlaceEquipments_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<InstalledEquipment_VersionStructure> otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment;

    public List<InstalledEquipment_VersionStructure> getOtherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment() {
        if (otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment == null) {
            otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment = new ArrayList<InstalledEquipment_VersionStructure>();
        }
        return this.otherPlaceEquipmentOrRoughSurfaceOrEntranceEquipment;
    }

}
