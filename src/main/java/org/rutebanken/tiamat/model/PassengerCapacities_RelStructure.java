package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PassengerCapacities_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<Object> passengerCapacityRefOrPassengerCapacity;

    public List<Object> getPassengerCapacityRefOrPassengerCapacity() {
        if (passengerCapacityRefOrPassengerCapacity == null) {
            passengerCapacityRefOrPassengerCapacity = new ArrayList<Object>();
        }
        return this.passengerCapacityRefOrPassengerCapacity;
    }

}
