package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypesOfFacility_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> typeOfFacilityRefOrTypeOfFacility;

    public List<Object> getTypeOfFacilityRefOrTypeOfFacility() {
        if (typeOfFacilityRefOrTypeOfFacility == null) {
            typeOfFacilityRefOrTypeOfFacility = new ArrayList<Object>();
        }
        return this.typeOfFacilityRefOrTypeOfFacility;
    }

}
