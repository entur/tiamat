package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public abstract class AbstractGroupMember_VersionedChildStructure
        extends VersionedChildStructure {

    protected MultilingualStringEntity description;
    protected BigInteger order;

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
