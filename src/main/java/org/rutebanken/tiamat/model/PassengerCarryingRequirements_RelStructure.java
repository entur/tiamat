package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PassengerCarryingRequirements_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> passengerCarryingRequirementRefOrPassengerCarryingRequirement;

    public List<Object> getPassengerCarryingRequirementRefOrPassengerCarryingRequirement() {
        if (passengerCarryingRequirementRefOrPassengerCarryingRequirement == null) {
            passengerCarryingRequirementRefOrPassengerCarryingRequirement = new ArrayList<Object>();
        }
        return this.passengerCarryingRequirementRefOrPassengerCarryingRequirement;
    }

}
