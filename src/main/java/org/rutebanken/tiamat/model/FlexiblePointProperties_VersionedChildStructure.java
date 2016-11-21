

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class FlexiblePointProperties_VersionedChildStructure
    extends VersionedChildStructure
{

    protected PointOnRouteRefStructure pointOnRouteRef;
    protected JAXBElement<? extends PointRefStructure> pointRef;
    protected Boolean mayBeSkipped;
    protected Boolean onMainRoute;
    protected Boolean pointStandingForAZone;
    protected Boolean zoneContainingStops;

    public PointOnRouteRefStructure getPointOnRouteRef() {
        return pointOnRouteRef;
    }

    public void setPointOnRouteRef(PointOnRouteRefStructure value) {
        this.pointOnRouteRef = value;
    }

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

    public Boolean isMayBeSkipped() {
        return mayBeSkipped;
    }

    public void setMayBeSkipped(Boolean value) {
        this.mayBeSkipped = value;
    }

    public Boolean isOnMainRoute() {
        return onMainRoute;
    }

    public void setOnMainRoute(Boolean value) {
        this.onMainRoute = value;
    }

    public Boolean isPointStandingForAZone() {
        return pointStandingForAZone;
    }

    public void setPointStandingForAZone(Boolean value) {
        this.pointStandingForAZone = value;
    }

    public Boolean isZoneContainingStops() {
        return zoneContainingStops;
    }

    public void setZoneContainingStops(Boolean value) {
        this.zoneContainingStops = value;
    }

}
