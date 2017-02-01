package org.rutebanken.tiamat.model;

public class PathLinkInSequence_VersionedChildStructure
        extends LinkInLinkSequence_VersionedChildStructure {

    protected PathLinkRefStructure pathLinkRef;
    protected MultilingualStringEntity description;
    protected Boolean reverse;
    protected PathHeadingEnumeration heading;
    protected TransitionEnumeration transition;
    protected MultilingualStringEntity label;
    protected Views views;

    public PathLinkRefStructure getPathLinkRef() {
        return pathLinkRef;
    }

    public void setPathLinkRef(PathLinkRefStructure value) {
        this.pathLinkRef = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public Boolean isReverse() {
        return reverse;
    }

    public void setReverse(Boolean value) {
        this.reverse = value;
    }

    public PathHeadingEnumeration getHeading() {
        return heading;
    }

    public void setHeading(PathHeadingEnumeration value) {
        this.heading = value;
    }

    public TransitionEnumeration getTransition() {
        return transition;
    }

    public void setTransition(TransitionEnumeration value) {
        this.transition = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public Views getViews() {
        return views;
    }

    public void setViews(Views value) {
        this.views = value;
    }

}
