

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


    "checkConstraintRef",
    "period",
    "maximumPassengers",
    "averagePassengers",
public class CheckConstraintThroughput_VersionStructure
    extends Assignment_VersionStructure
{

    protected CheckConstraintRefStructure checkConstraintRef;
    protected Duration period;
    protected BigInteger maximumPassengers;
    protected BigInteger averagePassengers;
    protected BigInteger wheelchairPassengers;

    public CheckConstraintRefStructure getCheckConstraintRef() {
        return checkConstraintRef;
    }

    public void setCheckConstraintRef(CheckConstraintRefStructure value) {
        this.checkConstraintRef = value;
    }

    public Duration getPeriod() {
        return period;
    }

    public void setPeriod(Duration value) {
        this.period = value;
    }

    public BigInteger getMaximumPassengers() {
        return maximumPassengers;
    }

    public void setMaximumPassengers(BigInteger value) {
        this.maximumPassengers = value;
    }

    public BigInteger getAveragePassengers() {
        return averagePassengers;
    }

    public void setAveragePassengers(BigInteger value) {
        this.averagePassengers = value;
    }

    public BigInteger getWheelchairPassengers() {
        return wheelchairPassengers;
    }

    public void setWheelchairPassengers(BigInteger value) {
        this.wheelchairPassengers = value;
    }

}
