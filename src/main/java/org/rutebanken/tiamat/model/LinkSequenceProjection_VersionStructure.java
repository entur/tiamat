package org.rutebanken.tiamat.model;

import net.opengis.gml._3.DeprecatedLineStringType;

import java.math.BigDecimal;


public class LinkSequenceProjection_VersionStructure
        extends Projection_VersionStructure {

    protected LinkSequenceRefStructure projectedLinkSequenceRef;
    protected BigDecimal distance;
    protected PointRefs_RelStructure points;
    protected DeprecatedLineStringType lineString;

    public LinkSequenceRefStructure getProjectedLinkSequenceRef() {
        return projectedLinkSequenceRef;
    }

    public void setProjectedLinkSequenceRef(LinkSequenceRefStructure value) {
        this.projectedLinkSequenceRef = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public PointRefs_RelStructure getPoints() {
        return points;
    }

    public void setPoints(PointRefs_RelStructure value) {
        this.points = value;
    }

    public DeprecatedLineStringType getLineString() {
        return lineString;
    }

    public void setLineString(DeprecatedLineStringType value) {
        this.lineString = value;
    }

}
