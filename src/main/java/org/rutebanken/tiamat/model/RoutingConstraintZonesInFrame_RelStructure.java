package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class RoutingConstraintZonesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<RoutingConstraintZone> routingConstraintZone;

    public List<RoutingConstraintZone> getRoutingConstraintZone() {
        if (routingConstraintZone == null) {
            routingConstraintZone = new ArrayList<RoutingConstraintZone>();
        }
        return this.routingConstraintZone;
    }

}
