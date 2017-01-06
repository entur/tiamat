package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;


public class PassengerStopAssignment_DerivedViewStructure
        extends DerivedViewStructure {

    protected JAXBElement<? extends PassengerStopAssignmentRefStructure> passengerStopAssignmentRef;
    protected StopPlaceReference stopPlaceRef;
    protected QuayReference quayRef;
    protected MultilingualStringEntity quayName;
    protected String label;
    protected BigInteger order;

    public JAXBElement<? extends PassengerStopAssignmentRefStructure> getPassengerStopAssignmentRef() {
        return passengerStopAssignmentRef;
    }

    public void setPassengerStopAssignmentRef(JAXBElement<? extends PassengerStopAssignmentRefStructure> value) {
        this.passengerStopAssignmentRef = value;
    }

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

    public MultilingualStringEntity getQuayName() {
        return quayName;
    }

    public void setQuayName(MultilingualStringEntity value) {
        this.quayName = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        this.label = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
