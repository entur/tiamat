

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class RoutingConstraintZonesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<RoutingConstraintZone> routingConstraintZone;

    public List<RoutingConstraintZone> getRoutingConstraintZone() {
        if (routingConstraintZone == null) {
            routingConstraintZone = new ArrayList<RoutingConstraintZone>();
        }
        return this.routingConstraintZone;
    }

}
