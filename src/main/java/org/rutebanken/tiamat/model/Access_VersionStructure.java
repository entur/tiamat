package org.rutebanken.tiamat.model;

public class Access_VersionStructure
        extends Transfer_VersionStructure {

    protected AccessEndStructure from;
    protected AccessEndStructure to;

    public AccessEndStructure getFrom() {
        return from;
    }

    public void setFrom(AccessEndStructure value) {
        this.from = value;
    }

    public AccessEndStructure getTo() {
        return to;
    }

    public void setTo(AccessEndStructure value) {
        this.to = value;
    }

}
