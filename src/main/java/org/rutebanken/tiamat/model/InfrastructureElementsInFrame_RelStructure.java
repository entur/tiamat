package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class InfrastructureElementsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<InfrastructureLink_VersionStructure> railwayElementOrRoadElementOrWireElement;

    public List<InfrastructureLink_VersionStructure> getRailwayElementOrRoadElementOrWireElement() {
        if (railwayElementOrRoadElementOrWireElement == null) {
            railwayElementOrRoadElementOrWireElement = new ArrayList<InfrastructureLink_VersionStructure>();
        }
        return this.railwayElementOrRoadElementOrWireElement;
    }

}
