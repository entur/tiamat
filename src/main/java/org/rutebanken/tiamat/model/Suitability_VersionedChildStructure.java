package org.rutebanken.tiamat.model;

public class Suitability_VersionedChildStructure
        extends UserNeed_VersionedChildStructure {

    protected SuitableEnumeration suitable;

    public SuitableEnumeration getSuitable() {
        return suitable;
    }

    public void setSuitable(SuitableEnumeration value) {
        this.suitable = value;
    }

}
