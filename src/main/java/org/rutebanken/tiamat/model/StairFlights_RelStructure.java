

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class StairFlights_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<StairFlight> stairFlight;

    public List<StairFlight> getStairFlight() {
        if (stairFlight == null) {
            stairFlight = new ArrayList<StairFlight>();
        }
        return this.stairFlight;
    }

}
