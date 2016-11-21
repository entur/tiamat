

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "continuousHandrail",
    "spiralStair",
    "numberOfFlights",
public class StaircaseEquipment_VersionStructure
    extends StairEquipment_VersionStructure
{

    protected Boolean continuousHandrail;
    protected Boolean spiralStair;
    protected BigInteger numberOfFlights;
    protected StairFlights_RelStructure flights;

    public Boolean isContinuousHandrail() {
        return continuousHandrail;
    }

    public void setContinuousHandrail(Boolean value) {
        this.continuousHandrail = value;
    }

    public Boolean isSpiralStair() {
        return spiralStair;
    }

    public void setSpiralStair(Boolean value) {
        this.spiralStair = value;
    }

    public BigInteger getNumberOfFlights() {
        return numberOfFlights;
    }

    public void setNumberOfFlights(BigInteger value) {
        this.numberOfFlights = value;
    }

    public StairFlights_RelStructure getFlights() {
        return flights;
    }

    public void setFlights(StairFlights_RelStructure value) {
        this.flights = value;
    }

}
