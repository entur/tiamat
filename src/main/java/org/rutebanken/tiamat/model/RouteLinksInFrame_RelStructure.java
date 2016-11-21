

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class RouteLinksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<RouteLink> routeLink;

    public List<RouteLink> getRouteLink() {
        if (routeLink == null) {
            routeLink = new ArrayList<RouteLink>();
        }
        return this.routeLink;
    }

}
