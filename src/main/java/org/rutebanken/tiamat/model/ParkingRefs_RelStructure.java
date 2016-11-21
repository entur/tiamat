

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ParkingRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<ParkingRefStructure> parkingRef;

    public List<ParkingRefStructure> getParkingRef() {
        if (parkingRef == null) {
            parkingRef = new ArrayList<ParkingRefStructure>();
        }
        return this.parkingRef;
    }

}
