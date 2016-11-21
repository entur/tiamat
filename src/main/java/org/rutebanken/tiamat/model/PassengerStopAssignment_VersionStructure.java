package org.rutebanken.tiamat.model;

public class PassengerStopAssignment_VersionStructure
        extends StopAssignment_VersionStructure {

    protected StopPlaceReference stopPlaceRef;
    protected QuayReference quayRef;
    protected BoardingPositionRefStructure boardingPositionRef;
    protected TrainElements trainElements;

    public StopPlaceReference getStopPlaceRef() {
        return stopPlaceRef;
    }

    public void setStopPlaceRef(StopPlaceReference value) {
        this.stopPlaceRef = value;
    }

    public QuayReference getQuayRef() {
        return quayRef;
    }

    public void setQuayRef(QuayReference value) {
        this.quayRef = value;
    }

    public BoardingPositionRefStructure getBoardingPositionRef() {
        return boardingPositionRef;
    }

    public void setBoardingPositionRef(BoardingPositionRefStructure value) {
        this.boardingPositionRef = value;
    }

    public TrainElements getTrainElements() {
        return trainElements;
    }

    public void setTrainElements(TrainElements value) {
        this.trainElements = value;
    }

}
