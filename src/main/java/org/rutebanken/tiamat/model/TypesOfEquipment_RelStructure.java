package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypesOfEquipment_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<Object> typeOfEquipmentRefOrTypeOfEquipment;

    public List<Object> getTypeOfEquipmentRefOrTypeOfEquipment() {
        if (typeOfEquipmentRefOrTypeOfEquipment == null) {
            typeOfEquipmentRefOrTypeOfEquipment = new ArrayList<Object>();
        }
        return this.typeOfEquipmentRefOrTypeOfEquipment;
    }

}
