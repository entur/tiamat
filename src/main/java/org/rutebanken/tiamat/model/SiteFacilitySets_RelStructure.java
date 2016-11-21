package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class SiteFacilitySets_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> siteFacilitySetRefOrSiteFacilitySet;

    public List<Object> getSiteFacilitySetRefOrSiteFacilitySet() {
        if (siteFacilitySetRefOrSiteFacilitySet == null) {
            siteFacilitySetRefOrSiteFacilitySet = new ArrayList<Object>();
        }
        return this.siteFacilitySetRefOrSiteFacilitySet;
    }

}
