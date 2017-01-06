package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingAreaRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<ParkingAreaRefStructure> parkingAreaRef;

    public List<ParkingAreaRefStructure> getParkingAreaRef() {
        if (parkingAreaRef == null) {
            parkingAreaRef = new ArrayList<ParkingAreaRefStructure>();
        }
        return this.parkingAreaRef;
    }

}
