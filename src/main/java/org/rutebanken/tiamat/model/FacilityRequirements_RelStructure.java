package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FacilityRequirements_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> facilityRequirementRefOrFacilityRequirement;

    public List<Object> getFacilityRequirementRefOrFacilityRequirement() {
        if (facilityRequirementRefOrFacilityRequirement == null) {
            facilityRequirementRefOrFacilityRequirement = new ArrayList<Object>();
        }
        return this.facilityRequirementRefOrFacilityRequirement;
    }

}
