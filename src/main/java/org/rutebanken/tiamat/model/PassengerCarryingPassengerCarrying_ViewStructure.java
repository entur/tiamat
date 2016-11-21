

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PassengerCarryingPassengerCarrying_ViewStructure
    extends DerivedViewStructure
{

    protected PassengerCapacity passengerCapacity;
    protected Boolean lowFloor;
    protected Boolean hasLiftOrRamp;
    protected Boolean hasHoist;

    public PassengerCapacity getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(PassengerCapacity value) {
        this.passengerCapacity = value;
    }

    public Boolean isLowFloor() {
        return lowFloor;
    }

    public void setLowFloor(Boolean value) {
        this.lowFloor = value;
    }

    public Boolean isHasLiftOrRamp() {
        return hasLiftOrRamp;
    }

    public void setHasLiftOrRamp(Boolean value) {
        this.hasLiftOrRamp = value;
    }

    public Boolean isHasHoist() {
        return hasHoist;
    }

    public void setHasHoist(Boolean value) {
        this.hasHoist = value;
    }

}
