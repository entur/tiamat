package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class RouteRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<RouteRefStructure> routeRef;

    public List<RouteRefStructure> getRouteRef() {
        if (routeRef == null) {
            routeRef = new ArrayList<RouteRefStructure>();
        }
        return this.routeRef;
    }

}
