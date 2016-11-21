

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "routeRef",
    "routeView",
    "directionType",
    "directionRef",
    "directionView",
    "destinationDisplayRef",
    "destinationDisplayView",
    "typeOfJourneyPatternRef",
    "operationalContextRef",
    "timingPatternRef",
    "runTimes",
    "waitTimes",
    "headways",
    "layovers",
    "journeyPatterns",
    "pointsInSequence",
public class ServicePattern_VersionStructure
    extends LinkSequence_VersionStructure
{

    protected RouteRefStructure routeRef;
    protected RouteView routeView;
    protected DirectionTypeEnumeration directionType;
    protected DirectionRefStructure directionRef;
    protected DirectionView directionView;
    protected DestinationDisplayRefStructure destinationDisplayRef;
    protected DestinationDisplayView destinationDisplayView;
    protected TypeOfJourneyPatternRefStructure typeOfJourneyPatternRef;
    protected OperationalContextRefStructure operationalContextRef;
    protected TimingPatternRefStructure timingPatternRef;
    protected JourneyPatternRunTimes_RelStructure runTimes;
    protected JourneyPatternWaitTimes_RelStructure waitTimes;
    protected JourneyPatternHeadways_RelStructure headways;
    protected JourneyPatternLayovers_RelStructure layovers;
    protected JourneyPatternRefs_RelStructure journeyPatterns;
    protected StopPointsInJourneyPattern_RelStructure pointsInSequence;
    protected ServiceLinksInJourneyPattern_RelStructure linksInSequence;

    public RouteRefStructure getRouteRef() {
        return routeRef;
    }

    public void setRouteRef(RouteRefStructure value) {
        this.routeRef = value;
    }

    public RouteView getRouteView() {
        return routeView;
    }

    public void setRouteView(RouteView value) {
        this.routeView = value;
    }

    public DirectionTypeEnumeration getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionTypeEnumeration value) {
        this.directionType = value;
    }

    public DirectionRefStructure getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(DirectionRefStructure value) {
        this.directionRef = value;
    }

    public DirectionView getDirectionView() {
        return directionView;
    }

    public void setDirectionView(DirectionView value) {
        this.directionView = value;
    }

    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

    public DestinationDisplayView getDestinationDisplayView() {
        return destinationDisplayView;
    }

    public void setDestinationDisplayView(DestinationDisplayView value) {
        this.destinationDisplayView = value;
    }

    public TypeOfJourneyPatternRefStructure getTypeOfJourneyPatternRef() {
        return typeOfJourneyPatternRef;
    }

    public void setTypeOfJourneyPatternRef(TypeOfJourneyPatternRefStructure value) {
        this.typeOfJourneyPatternRef = value;
    }

    public OperationalContextRefStructure getOperationalContextRef() {
        return operationalContextRef;
    }

    public void setOperationalContextRef(OperationalContextRefStructure value) {
        this.operationalContextRef = value;
    }

    public TimingPatternRefStructure getTimingPatternRef() {
        return timingPatternRef;
    }

    public void setTimingPatternRef(TimingPatternRefStructure value) {
        this.timingPatternRef = value;
    }

    public JourneyPatternRunTimes_RelStructure getRunTimes() {
        return runTimes;
    }

    public void setRunTimes(JourneyPatternRunTimes_RelStructure value) {
        this.runTimes = value;
    }

    public JourneyPatternWaitTimes_RelStructure getWaitTimes() {
        return waitTimes;
    }

    public void setWaitTimes(JourneyPatternWaitTimes_RelStructure value) {
        this.waitTimes = value;
    }

    public JourneyPatternHeadways_RelStructure getHeadways() {
        return headways;
    }

    public void setHeadways(JourneyPatternHeadways_RelStructure value) {
        this.headways = value;
    }

    public JourneyPatternLayovers_RelStructure getLayovers() {
        return layovers;
    }

    public void setLayovers(JourneyPatternLayovers_RelStructure value) {
        this.layovers = value;
    }

    public JourneyPatternRefs_RelStructure getJourneyPatterns() {
        return journeyPatterns;
    }

    public void setJourneyPatterns(JourneyPatternRefs_RelStructure value) {
        this.journeyPatterns = value;
    }

    public StopPointsInJourneyPattern_RelStructure getPointsInSequence() {
        return pointsInSequence;
    }

    public void setPointsInSequence(StopPointsInJourneyPattern_RelStructure value) {
        this.pointsInSequence = value;
    }

    public ServiceLinksInJourneyPattern_RelStructure getLinksInSequence() {
        return linksInSequence;
    }

    public void setLinksInSequence(ServiceLinksInJourneyPattern_RelStructure value) {
        this.linksInSequence = value;
    }

}
