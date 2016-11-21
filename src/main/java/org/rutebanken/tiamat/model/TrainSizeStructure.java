

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
