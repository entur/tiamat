package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Routes_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> routeRefOrRoute;

    public List<Object> getRouteRefOrRoute() {
        if (routeRefOrRoute == null) {
            routeRefOrRoute = new ArrayList<Object>();
        }
        return this.routeRefOrRoute;
    }

}
