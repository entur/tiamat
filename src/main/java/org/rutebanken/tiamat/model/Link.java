package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.LineString;
import net.opengis.gml._3.LineStringType;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;

@MappedSuperclass
public abstract class Link extends DataManagedObjectStructure {

    private LineString lineString;

    public Link() {
    }

    public Link(LineString lineString) {
        this.lineString = lineString;
    }

    @Transient
    protected MultilingualStringEntity name;

    @Transient
    protected BigDecimal distance;

    @Transient
    protected LinkTypeRefs_RelStructure types;

    @Transient
    protected Projections_RelStructure projections;

    @Transient
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

    public LineString getLineString() {
        return lineString;
    }

    public void setLineString(LineString lineString) {
        this.lineString = lineString;
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
