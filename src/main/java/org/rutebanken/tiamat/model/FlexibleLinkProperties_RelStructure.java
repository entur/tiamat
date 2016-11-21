package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FlexibleLinkProperties_RelStructure
        extends ContainmentAggregationStructure {

    protected List<FlexibleLinkProperties> flexibleLinkProperties;

    public List<FlexibleLinkProperties> getFlexibleLinkProperties() {
        if (flexibleLinkProperties == null) {
            flexibleLinkProperties = new ArrayList<FlexibleLinkProperties>();
        }
        return this.flexibleLinkProperties;
    }

}
