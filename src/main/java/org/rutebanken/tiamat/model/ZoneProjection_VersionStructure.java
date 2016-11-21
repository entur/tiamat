

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "projectedZoneRef",
    "projectToZoneRef",
    "projectToPointRef",
public class ZoneProjection_VersionStructure
    extends Projection_VersionStructure
{

    protected ZoneRefStructure projectedZoneRef;
    protected ZoneRefStructure projectToZoneRef;
    protected PointRefStructure projectToPointRef;
    protected PointRefs_RelStructure points;

    public ZoneRefStructure getProjectedZoneRef() {
        return projectedZoneRef;
    }

    public void setProjectedZoneRef(ZoneRefStructure value) {
        this.projectedZoneRef = value;
    }

    public ZoneRefStructure getProjectToZoneRef() {
        return projectToZoneRef;
    }

    public void setProjectToZoneRef(ZoneRefStructure value) {
        this.projectToZoneRef = value;
    }

    public PointRefStructure getProjectToPointRef() {
        return projectToPointRef;
    }

    public void setProjectToPointRef(PointRefStructure value) {
        this.projectToPointRef = value;
    }

    public PointRefs_RelStructure getPoints() {
        return points;
    }

    public void setPoints(PointRefs_RelStructure value) {
        this.points = value;
    }

}
