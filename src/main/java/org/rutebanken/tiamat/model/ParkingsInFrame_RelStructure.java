

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ParkingsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Parking> parking;

    public List<Parking> getParking() {
        if (parking == null) {
            parking = new ArrayList<Parking>();
        }
        return this.parking;
    }

}
