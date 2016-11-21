

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "passengerStopAssignmentRef",
    "stopPlaceRef",
    "quayRef",
    "quayName",
public class PassengerStopAssignment_DerivedViewStructure
    extends DerivedViewStructure
{

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
