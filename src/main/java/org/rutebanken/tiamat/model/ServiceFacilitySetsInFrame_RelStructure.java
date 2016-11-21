package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ServiceFacilitySetsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ServiceFacilitySet> serviceFacilitySet;

    public List<ServiceFacilitySet> getServiceFacilitySet() {
        if (serviceFacilitySet == null) {
            serviceFacilitySet = new ArrayList<ServiceFacilitySet>();
        }
        return this.serviceFacilitySet;
    }

}
