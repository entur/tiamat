

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TimingPoints_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<TimingPoint> timingPoint;

    public List<TimingPoint> getTimingPoint() {
        if (timingPoint == null) {
            timingPoint = new ArrayList<TimingPoint>();
        }
        return this.timingPoint;
    }

}
