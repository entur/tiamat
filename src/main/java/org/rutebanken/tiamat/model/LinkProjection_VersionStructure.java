package org.rutebanken.tiamat.model;

public class LinkProjection_VersionStructure
        extends Projection_VersionStructure {

    protected LinkRefStructure projectedLinkRef;
    protected LinkRefStructure projectToLinkRef;
    protected PointOnLinkRefStructure startPointOnLinkRef;
    protected PointOnLinkByValueStructure startPointOnLinkByValue;
    protected PointOnLinkRefStructure endPointOnLinkRef;
    protected PointOnLinkByValueStructure endPointOnLinkByValue;

    public LinkRefStructure getProjectedLinkRef() {
        return projectedLinkRef;
    }

    public void setProjectedLinkRef(LinkRefStructure value) {
        this.projectedLinkRef = value;
    }

    public LinkRefStructure getProjectToLinkRef() {
        return projectToLinkRef;
    }

    public void setProjectToLinkRef(LinkRefStructure value) {
        this.projectToLinkRef = value;
    }

    public PointOnLinkRefStructure getStartPointOnLinkRef() {
        return startPointOnLinkRef;
    }

    public void setStartPointOnLinkRef(PointOnLinkRefStructure value) {
        this.startPointOnLinkRef = value;
    }

    public PointOnLinkByValueStructure getStartPointOnLinkByValue() {
        return startPointOnLinkByValue;
    }

    public void setStartPointOnLinkByValue(PointOnLinkByValueStructure value) {
        this.startPointOnLinkByValue = value;
    }

    public PointOnLinkRefStructure getEndPointOnLinkRef() {
        return endPointOnLinkRef;
    }

    public void setEndPointOnLinkRef(PointOnLinkRefStructure value) {
        this.endPointOnLinkRef = value;
    }

    public PointOnLinkByValueStructure getEndPointOnLinkByValue() {
        return endPointOnLinkByValue;
    }

    public void setEndPointOnLinkByValue(PointOnLinkByValueStructure value) {
        this.endPointOnLinkByValue = value;
    }

}
