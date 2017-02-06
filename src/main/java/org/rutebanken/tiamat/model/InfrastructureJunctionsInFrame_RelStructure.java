package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class InfrastructureJunctionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<InfrastructurePoint_> railwayJunctionOrRoadJunctionOrWireJunction;

    public List<InfrastructurePoint_> getRailwayJunctionOrRoadJunctionOrWireJunction() {
        if (railwayJunctionOrRoadJunctionOrWireJunction == null) {
            railwayJunctionOrRoadJunctionOrWireJunction = new ArrayList<InfrastructurePoint_>();
        }
        return this.railwayJunctionOrRoadJunctionOrWireJunction;
    }

}
