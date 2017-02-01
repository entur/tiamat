package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class AccessSummary_VersionedChildStructure
        extends VersionedChildStructure {

    protected AccessFeatureEnumeration accessFeatureType;
    protected BigInteger count;
    protected TransitionEnumeration transition;

    public AccessFeatureEnumeration getAccessFeatureType() {
        return accessFeatureType;
    }

    public void setAccessFeatureType(AccessFeatureEnumeration value) {
        this.accessFeatureType = value;
    }

    public BigInteger getCount() {
        return count;
    }

    public void setCount(BigInteger value) {
        this.count = value;
    }

    public TransitionEnumeration getTransition() {
        return transition;
    }

    public void setTransition(TransitionEnumeration value) {
        this.transition = value;
    }

}
