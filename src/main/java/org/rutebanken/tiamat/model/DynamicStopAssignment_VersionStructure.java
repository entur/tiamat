package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class DynamicStopAssignment_VersionStructure
        extends PassengerStopAssignment_VersionStructure {

    protected JAXBElement<? extends PassengerStopAssignmentRefStructure> passengerStopAssignmentRef;

    public JAXBElement<? extends PassengerStopAssignmentRefStructure> getPassengerStopAssignmentRef() {
        return passengerStopAssignmentRef;
    }

    public void setPassengerStopAssignmentRef(JAXBElement<? extends PassengerStopAssignmentRefStructure> value) {
        this.passengerStopAssignmentRef = value;
    }

}
