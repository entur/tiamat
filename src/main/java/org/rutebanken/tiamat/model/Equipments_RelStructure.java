package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class Equipments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> equipmentRefOrEquipment;

    public List<JAXBElement<?>> getEquipmentRefOrEquipment() {
        if (equipmentRefOrEquipment == null) {
            equipmentRefOrEquipment = new ArrayList<JAXBElement<?>>();
        }
        return this.equipmentRefOrEquipment;
    }

}
