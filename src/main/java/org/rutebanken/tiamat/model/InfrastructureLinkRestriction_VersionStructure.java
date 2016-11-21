package org.rutebanken.tiamat.model;

public abstract class InfrastructureLinkRestriction_VersionStructure
        extends NetworkRestriction_VersionStructure {

    protected LinkRefStructure fromLinkRef;
    protected LinkRefStructure toLinkRef;

    public LinkRefStructure getFromLinkRef() {
        return fromLinkRef;
    }

    public void setFromLinkRef(LinkRefStructure value) {
        this.fromLinkRef = value;
    }

    public LinkRefStructure getToLinkRef() {
        return toLinkRef;
    }

    public void setToLinkRef(LinkRefStructure value) {
        this.toLinkRef = value;
    }

}
