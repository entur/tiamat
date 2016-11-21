package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;
import java.math.BigInteger;


public class CheckConstraintThroughput_VersionStructure
        extends Assignment_VersionStructure {

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
