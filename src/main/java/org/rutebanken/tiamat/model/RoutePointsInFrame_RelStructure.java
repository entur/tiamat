

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class RoutePointsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<RoutePoint> routePoint;

    public List<RoutePoint> getRoutePoint() {
        if (routePoint == null) {
            routePoint = new ArrayList<RoutePoint>();
        }
        return this.routePoint;
    }

}
