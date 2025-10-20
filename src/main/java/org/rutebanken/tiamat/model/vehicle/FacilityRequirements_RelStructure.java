package org.rutebanken.tiamat.model.vehicle;

import org.rutebanken.tiamat.model.ContainmentAggregationStructure;

import java.util.ArrayList;
import java.util.List;

public class FacilityRequirements_RelStructure extends ContainmentAggregationStructure {

    private List<Object> facilityRequirementRefOrFacilityRequirement;

    public List<Object> getFacilityRequirementRefOrFacilityRequirement() {
        if (this.facilityRequirementRefOrFacilityRequirement == null) {
            this.facilityRequirementRefOrFacilityRequirement = new ArrayList();
        }

        return this.facilityRequirementRefOrFacilityRequirement;
    }
}
