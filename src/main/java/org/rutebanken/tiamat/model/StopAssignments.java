package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopAssignments {

    protected List<PassengerStopAssignment> passengerStopAssignment;

    public List<PassengerStopAssignment> getPassengerStopAssignment() {
        if (passengerStopAssignment == null) {
            passengerStopAssignment = new ArrayList<PassengerStopAssignment>();
        }
        return this.passengerStopAssignment;
    }

}
