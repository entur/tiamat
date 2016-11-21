

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class RouteRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<RouteRefStructure> routeRef;

    public List<RouteRefStructure> getRouteRef() {
        if (routeRef == null) {
            routeRef = new ArrayList<RouteRefStructure>();
        }
        return this.routeRef;
    }

}
