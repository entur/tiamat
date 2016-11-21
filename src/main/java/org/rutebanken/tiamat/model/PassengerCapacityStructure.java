

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class PassengerCapacityStructure
    extends DataManagedObjectStructure
{
    protected BigInteger totalCapacity;
    protected BigInteger seatingCapacity;
    protected BigInteger standingCapacity;
    protected BigInteger specialPlaceCapacity;
    protected BigInteger pushchairCapacity;
    protected BigInteger wheelchairPlaceCapacity;

    public BigInteger getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(BigInteger value) {
        this.totalCapacity = value;
    }

    public BigInteger getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(BigInteger value) {
        this.seatingCapacity = value;
    }

    public BigInteger getStandingCapacity() {
        return standingCapacity;
    }

    public void setStandingCapacity(BigInteger value) {
        this.standingCapacity = value;
    }

    public BigInteger getSpecialPlaceCapacity() {
        return specialPlaceCapacity;
    }

    public void setSpecialPlaceCapacity(BigInteger value) {
        this.specialPlaceCapacity = value;
    }

    public BigInteger getPushchairCapacity() {
        return pushchairCapacity;
    }

    public void setPushchairCapacity(BigInteger value) {
        this.pushchairCapacity = value;
    }

    public BigInteger getWheelchairPlaceCapacity() {
        return wheelchairPlaceCapacity;
    }

    public void setWheelchairPlaceCapacity(BigInteger value) {
        this.wheelchairPlaceCapacity = value;
    }

}
