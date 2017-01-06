package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingBayRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<ParkingBayRefStructure> parkingBayRef;

    public List<ParkingBayRefStructure> getParkingBayRef() {
        if (parkingBayRef == null) {
            parkingBayRef = new ArrayList<ParkingBayRefStructure>();
        }
        return this.parkingBayRef;
    }

}
