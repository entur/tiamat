package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class InfrastructureElementsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<InfrastructureLink_> railwayElementOrRoadElementOrWireElement;

    public List<InfrastructureLink_> getRailwayElementOrRoadElementOrWireElement() {
        if (railwayElementOrRoadElementOrWireElement == null) {
            railwayElementOrRoadElementOrWireElement = new ArrayList<InfrastructureLink_>();
        }
        return this.railwayElementOrRoadElementOrWireElement;
    }

}
