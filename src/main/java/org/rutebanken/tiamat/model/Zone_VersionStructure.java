package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.*;


@MappedSuperclass
public class Zone_VersionStructure
        extends GroupOfPoints_VersionStructure {

    @Transient
    protected TypeOfZoneRefs_RelStructure types;

    protected Point centroid;

    @Basic(fetch = FetchType.LAZY)
    protected Polygon polygon;

    @Transient
    protected Projections_RelStructure projections;

    @Transient
    protected ZoneRefStructure parentZoneRef;

    public Zone_VersionStructure() {
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Zone_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public TypeOfZoneRefs_RelStructure getTypes() {
        return types;
    }

    public void setTypes(TypeOfZoneRefs_RelStructure value) {
        this.types = value;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point value) {
        this.centroid = value;
    }

    public Projections_RelStructure getProjections() {
        return projections;
    }

    public void setProjections(Projections_RelStructure value) {
        this.projections = value;
    }

    public ZoneRefStructure getParentZoneRef() {
        return parentZoneRef;
    }

    public void setParentZoneRef(ZoneRefStructure value) {
        this.parentZoneRef = value;
    }

    public boolean hasCoordinates() {
        return centroid != null;
    }
}
