

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DestinationDisplaysInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<DestinationDisplay> destinationDisplay;

    public List<DestinationDisplay> getDestinationDisplay() {
        if (destinationDisplay == null) {
            destinationDisplay = new ArrayList<DestinationDisplay>();
        }
        return this.destinationDisplay;
    }

}
