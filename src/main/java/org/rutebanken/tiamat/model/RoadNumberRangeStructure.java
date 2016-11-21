

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


    "fromNumber",
public class RoadNumberRangeStructure {

    protected BigInteger fromNumber;
    protected BigInteger toNumber;

    public BigInteger getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(BigInteger value) {
        this.fromNumber = value;
    }

    public BigInteger getToNumber() {
        return toNumber;
    }

    public void setToNumber(BigInteger value) {
        this.toNumber = value;
    }

}
