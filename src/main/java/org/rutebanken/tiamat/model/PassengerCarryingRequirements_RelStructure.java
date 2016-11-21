

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class PassengerCarryingRequirements_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> passengerCarryingRequirementRefOrPassengerCarryingRequirement;

    public List<Object> getPassengerCarryingRequirementRefOrPassengerCarryingRequirement() {
        if (passengerCarryingRequirementRefOrPassengerCarryingRequirement == null) {
            passengerCarryingRequirementRefOrPassengerCarryingRequirement = new ArrayList<Object>();
        }
        return this.passengerCarryingRequirementRefOrPassengerCarryingRequirement;
    }

}
