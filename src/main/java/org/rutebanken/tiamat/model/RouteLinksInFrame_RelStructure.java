package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class RouteLinksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<RouteLink> routeLink;

    public List<RouteLink> getRouteLink() {
        if (routeLink == null) {
            routeLink = new ArrayList<RouteLink>();
        }
        return this.routeLink;
    }

}
