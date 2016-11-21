

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class ParkingAreas_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> parkingAreaRefOrParkingArea;

    public List<Object> getParkingAreaRefOrParkingArea() {
        if (parkingAreaRefOrParkingArea == null) {
            parkingAreaRefOrParkingArea = new ArrayList<Object>();
        }
        return this.parkingAreaRefOrParkingArea;
    }

}
