package org.rutebanken.tiamat.model;

import java.math.BigInteger;


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
