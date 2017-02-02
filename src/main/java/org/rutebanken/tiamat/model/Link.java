package org.rutebanken.tiamat.model;

import net.opengis.gml._3.LineStringType;

import java.math.BigDecimal;


public abstract class Link
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected BigDecimal distance;
    protected LinkTypeRefs_RelStructure types;
    protected LineStringType lineString;
    protected Projections_RelStructure projections;
    protected PointsOnLink_RelStructure passingThrough;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public LinkTypeRefs_RelStructure getTypes() {
        return types;
    }

    public void setTypes(LinkTypeRefs_RelStructure value) {
        this.types = value;
    }

    public LineStringType getLineString() {
        return lineString;
    }

    public void setLineString(LineStringType value) {
        this.lineString = value;
    }

    public Projections_RelStructure getProjections() {
        return projections;
    }

    public void setProjections(Projections_RelStructure value) {
        this.projections = value;
    }

    public PointsOnLink_RelStructure getPassingThrough() {
        return passingThrough;
    }

    public void setPassingThrough(PointsOnLink_RelStructure value) {
        this.passingThrough = value;
    }

}
