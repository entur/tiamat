

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class EquipmentPlaces_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> equipmentPlaceRefOrEquipmentPlace;

    public List<Object> getEquipmentPlaceRefOrEquipmentPlace() {
        if (equipmentPlaceRefOrEquipmentPlace == null) {
            equipmentPlaceRefOrEquipmentPlace = new ArrayList<Object>();
        }
        return this.equipmentPlaceRefOrEquipmentPlace;
    }

}
