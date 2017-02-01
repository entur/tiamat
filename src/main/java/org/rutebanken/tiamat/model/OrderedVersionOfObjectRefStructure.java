package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class OrderedVersionOfObjectRefStructure
        extends VersionOfObjectRefStructure {

    protected BigInteger order;

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
