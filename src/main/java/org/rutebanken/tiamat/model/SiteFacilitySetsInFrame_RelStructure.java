package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class SiteFacilitySetsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<SiteFacilitySet> siteFacilitySet;

    public List<SiteFacilitySet> getSiteFacilitySet() {
        if (siteFacilitySet == null) {
            siteFacilitySet = new ArrayList<SiteFacilitySet>();
        }
        return this.siteFacilitySet;
    }

}
