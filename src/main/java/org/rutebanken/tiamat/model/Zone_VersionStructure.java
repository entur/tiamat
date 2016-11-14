package org.rutebanken.tiamat.model;

import com.vividsolutions.jts.geom.Point;
import net.opengis.gml._3.PolygonType;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.Optional;


@MappedSuperclass
public class Zone_VersionStructure
    extends GroupOfPoints_VersionStructure
{

    @Transient
    protected TypeOfZoneRefs_RelStructure types;

    protected Point centroid;

    @XmlElement(name = "Polygon", namespace = "http://www.opengis.net/gml/3.2")
    @Transient
    protected PolygonType polygon;

    @Transient
    protected Projections_RelStructure projections;

    @XmlElement(name = "ParentZoneRef")
    @Transient
    protected ZoneRefStructure parentZoneRef;

    public Zone_VersionStructure() {}

    public Zone_VersionStructure(MultilingualString name) {
        super(name);
    }

    /**
     * Gets the value of the types property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOfZoneRefs_RelStructure }
     *     
     */
    public TypeOfZoneRefs_RelStructure getTypes() {
        return types;
    }

    /**
     * Sets the value of the types property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOfZoneRefs_RelStructure }
     *     
     */
    public void setTypes(TypeOfZoneRefs_RelStructure value) {
        this.types = value;
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point value) {
        this.centroid = value;
    }

    /**
     * Gets the value of the polygon property.
     * 
     * @return
     *     possible object is
     *     {@link PolygonType }
     *     
     */
    public PolygonType getPolygon() {
        return polygon;
    }

    /**
     * Sets the value of the polygon property.
     * 
     * @param value
     *     allowed object is
     *     {@link PolygonType }
     *     
     */
    public void setPolygon(PolygonType value) {
        this.polygon = value;
    }

    /**
     * Gets the value of the projections property.
     * 
     * @return
     *     possible object is
     *     {@link Projections_RelStructure }
     *     
     */
    public Projections_RelStructure getProjections() {
        return projections;
    }

    /**
     * Sets the value of the projections property.
     * 
     * @param value
     *     allowed object is
     *     {@link Projections_RelStructure }
     *     
     */
    public void setProjections(Projections_RelStructure value) {
        this.projections = value;
    }

    /**
     * Gets the value of the parentZoneRef property.
     * 
     * @return
     *     possible object is
     *     {@link ZoneRefStructure }
     *     
     */
    public ZoneRefStructure getParentZoneRef() {
        return parentZoneRef;
    }

    /**
     * Sets the value of the parentZoneRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ZoneRefStructure }
     *     
     */
    public void setParentZoneRef(ZoneRefStructure value) {
        this.parentZoneRef = value;
    }

    public boolean hasCoordinates() {
        return centroid != null;
    }
}
