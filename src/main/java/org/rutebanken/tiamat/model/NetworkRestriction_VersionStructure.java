package org.rutebanken.tiamat.model;

public abstract class NetworkRestriction_VersionStructure
        extends Assignment_VersionStructure {

    protected boolean restricted;

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean value) {
        this.restricted = value;
    }

}
