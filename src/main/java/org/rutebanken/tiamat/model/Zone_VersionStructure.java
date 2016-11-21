package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Point;
import net.opengis.gml._3.PolygonType;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;


@MappedSuperclass
public class Zone_VersionStructure
    extends GroupOfPoints_VersionStructure
{

    @Transient
    protected TypeOfZoneRefs_RelStructure types;

    protected Point centroid;

    @Transient
    protected PolygonType polygon;

    @Transient
    protected Projections_RelStructure projections;

    @Transient
    protected ZoneRefStructure parentZoneRef;

    public Zone_VersionStructure() {}

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

    public PolygonType getPolygon() {
        return polygon;
    }

    public void setPolygon(PolygonType value) {
        this.polygon = value;
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
