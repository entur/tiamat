package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ServiceFacilitySets_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> serviceFacilitySetRefOrServiceFacilitySet;

    public List<Object> getServiceFacilitySetRefOrServiceFacilitySet() {
        if (serviceFacilitySetRefOrServiceFacilitySet == null) {
            serviceFacilitySetRefOrServiceFacilitySet = new ArrayList<Object>();
        }
        return this.serviceFacilitySetRefOrServiceFacilitySet;
    }

}
