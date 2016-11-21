

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "routeRef",
    "directionType",
    "timeDemandTypeRef",
    "timebandRef",
    "pointsInSequence",
    "points",
public class TimingPattern_VersionStructure
    extends LinkSequence_VersionStructure
{

    protected RouteRefStructure routeRef;
    protected DirectionTypeEnumeration directionType;
    protected TimeDemandTypeRefStructure timeDemandTypeRef;
    protected TimebandRefStructure timebandRef;
    protected TimingPointsInJourneyPattern_RelStructure pointsInSequence;
    protected TimingPoints_RelStructure points;
    protected TimingLinks_RelStructure links;

    public RouteRefStructure getRouteRef() {
        return routeRef;
    }

    public void setRouteRef(RouteRefStructure value) {
        this.routeRef = value;
    }

    public DirectionTypeEnumeration getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionTypeEnumeration value) {
        this.directionType = value;
    }

    public TimeDemandTypeRefStructure getTimeDemandTypeRef() {
        return timeDemandTypeRef;
    }

    public void setTimeDemandTypeRef(TimeDemandTypeRefStructure value) {
        this.timeDemandTypeRef = value;
    }

    public TimebandRefStructure getTimebandRef() {
        return timebandRef;
    }

    public void setTimebandRef(TimebandRefStructure value) {
        this.timebandRef = value;
    }

    public TimingPointsInJourneyPattern_RelStructure getPointsInSequence() {
        return pointsInSequence;
    }

    public void setPointsInSequence(TimingPointsInJourneyPattern_RelStructure value) {
        this.pointsInSequence = value;
    }

    public TimingPoints_RelStructure getPoints() {
        return points;
    }

    public void setPoints(TimingPoints_RelStructure value) {
        this.points = value;
    }

    public TimingLinks_RelStructure getLinks() {
        return links;
    }

    public void setLinks(TimingLinks_RelStructure value) {
        this.links = value;
    }

}
