package org.rutebanken.tiamat.model;

public class StopPlaces_RelStructure
        extends ContainmentAggregationStructure {

    protected StopPlaceReference stopPlaceRef;
    protected StopPlace stopPlace;

    public StopPlaceReference getStopPlaceRef() {
        return stopPlaceRef;
    }

    public void setStopPlaceRef(StopPlaceReference value) {
        this.stopPlaceRef = value;
    }

    public StopPlace getStopPlace() {
        return stopPlace;
    }

    public void setStopPlace(StopPlace value) {
        this.stopPlace = value;
    }

}
