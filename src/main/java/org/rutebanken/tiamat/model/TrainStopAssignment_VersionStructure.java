package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;


public class TrainStopAssignment_VersionStructure
        extends StopAssignment_VersionStructure {

    protected JAXBElement<? extends PassengerStopAssignmentRefStructure> passengerStopAssignmentRef;
    protected TrainRefStructure trainRef;
    protected TrainComponentRefStructure trainComponentRef;
    protected TrainComponentView trainComponentView;
    protected BigInteger positionOfTrainElement;
    protected BoardingPositionRefStructure boardingPositionRef;
    protected MultilingualStringEntity entranceToVehicle;

    public JAXBElement<? extends PassengerStopAssignmentRefStructure> getPassengerStopAssignmentRef() {
        return passengerStopAssignmentRef;
    }

    public void setPassengerStopAssignmentRef(JAXBElement<? extends PassengerStopAssignmentRefStructure> value) {
        this.passengerStopAssignmentRef = value;
    }

    public TrainRefStructure getTrainRef() {
        return trainRef;
    }

    public void setTrainRef(TrainRefStructure value) {
        this.trainRef = value;
    }

    public TrainComponentRefStructure getTrainComponentRef() {
        return trainComponentRef;
    }

    public void setTrainComponentRef(TrainComponentRefStructure value) {
        this.trainComponentRef = value;
    }

    public TrainComponentView getTrainComponentView() {
        return trainComponentView;
    }

    public void setTrainComponentView(TrainComponentView value) {
        this.trainComponentView = value;
    }

    public BigInteger getPositionOfTrainElement() {
        return positionOfTrainElement;
    }

    public void setPositionOfTrainElement(BigInteger value) {
        this.positionOfTrainElement = value;
    }

    public BoardingPositionRefStructure getBoardingPositionRef() {
        return boardingPositionRef;
    }

    public void setBoardingPositionRef(BoardingPositionRefStructure value) {
        this.boardingPositionRef = value;
    }

    public MultilingualStringEntity getEntranceToVehicle() {
        return entranceToVehicle;
    }

    public void setEntranceToVehicle(MultilingualStringEntity value) {
        this.entranceToVehicle = value;
    }

}
