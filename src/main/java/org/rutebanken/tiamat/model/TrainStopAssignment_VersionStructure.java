/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
