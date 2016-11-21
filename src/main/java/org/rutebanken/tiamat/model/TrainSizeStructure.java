package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class TrainSizeStructure {

    protected BigInteger numberOfCars;
    protected TrainSizeEnumeration trainSizeType;

    public BigInteger getNumberOfCars() {
        return numberOfCars;
    }

    public void setNumberOfCars(BigInteger value) {
        this.numberOfCars = value;
    }

    public TrainSizeEnumeration getTrainSizeType() {
        return trainSizeType;
    }

    public void setTrainSizeType(TrainSizeEnumeration value) {
        this.trainSizeType = value;
    }

}
