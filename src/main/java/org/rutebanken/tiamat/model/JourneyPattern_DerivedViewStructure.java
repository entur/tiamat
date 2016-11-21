package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class JourneyPattern_DerivedViewStructure
        extends DerivedViewStructure {

    protected JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRef;
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

    public JAXBElement<? extends JourneyPatternRefStructure> getJourneyPatternRef() {
        return journeyPatternRef;
    }

    public void setJourneyPatternRef(JAXBElement<? extends JourneyPatternRefStructure> value) {
        this.journeyPatternRef = value;
    }

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

}
