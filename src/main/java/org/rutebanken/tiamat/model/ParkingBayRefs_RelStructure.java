

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ParkingBayRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<ParkingBayRefStructure> parkingBayRef;

    public List<ParkingBayRefStructure> getParkingBayRef() {
        if (parkingBayRef == null) {
            parkingBayRef = new ArrayList<ParkingBayRefStructure>();
        }
        return this.parkingBayRef;
    }

}
