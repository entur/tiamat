

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class StopAreasInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<StopArea> stopArea;

    public List<StopArea> getStopArea() {
        if (stopArea == null) {
            stopArea = new ArrayList<StopArea>();
        }
        return this.stopArea;
    }

}
