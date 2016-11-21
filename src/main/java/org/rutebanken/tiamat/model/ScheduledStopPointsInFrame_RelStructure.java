

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ScheduledStopPointsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ScheduledStopPoint> scheduledStopPoint;

    public List<ScheduledStopPoint> getScheduledStopPoint() {
        if (scheduledStopPoint == null) {
            scheduledStopPoint = new ArrayList<ScheduledStopPoint>();
        }
        return this.scheduledStopPoint;
    }

}
