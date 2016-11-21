

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TrafficControlPointsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<TrafficControlPoint> trafficControlPoint;

    public List<TrafficControlPoint> getTrafficControlPoint() {
        if (trafficControlPoint == null) {
            trafficControlPoint = new ArrayList<TrafficControlPoint>();
        }
        return this.trafficControlPoint;
    }

}
