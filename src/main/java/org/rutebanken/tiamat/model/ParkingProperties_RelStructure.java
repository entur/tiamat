

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ParkingProperties_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<ParkingProperties> parkingProperties;

    public List<ParkingProperties> getParkingProperties() {
        if (parkingProperties == null) {
            parkingProperties = new ArrayList<ParkingProperties>();
        }
        return this.parkingProperties;
    }

}
