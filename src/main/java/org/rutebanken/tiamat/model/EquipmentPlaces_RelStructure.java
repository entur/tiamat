package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class EquipmentPlaces_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> equipmentPlaceRefOrEquipmentPlace;

    public List<Object> getEquipmentPlaceRefOrEquipmentPlace() {
        if (equipmentPlaceRefOrEquipmentPlace == null) {
            equipmentPlaceRefOrEquipmentPlace = new ArrayList<Object>();
        }
        return this.equipmentPlaceRefOrEquipmentPlace;
    }

}
