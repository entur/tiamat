package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class PlaceEquipments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> installedEquipmentRefOrInstalledEquipment;

    public List<JAXBElement<?>> getInstalledEquipmentRefOrInstalledEquipment() {
        if (installedEquipmentRefOrInstalledEquipment == null) {
            installedEquipmentRefOrInstalledEquipment = new ArrayList<JAXBElement<?>>();
        }
        return this.installedEquipmentRefOrInstalledEquipment;
    }

}
