package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FlexiblePointProperties_RelStructure
        extends ContainmentAggregationStructure {

    protected List<FlexiblePointProperties> flexiblePointProperties;

    public List<FlexiblePointProperties> getFlexiblePointProperties() {
        if (flexiblePointProperties == null) {
            flexiblePointProperties = new ArrayList<FlexiblePointProperties>();
        }
        return this.flexiblePointProperties;
    }

}
