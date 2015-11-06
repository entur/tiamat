//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a PASSENGER STOP ASSIGNMENT.
 * 
 * <p>Java class for PassengerStopAssignment_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PassengerStopAssignment_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}StopAssignment_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}PassengerStopAssignmentGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PassengerStopAssignment_VersionStructure", propOrder = {
    "stopPlaceRef",
    "quayRef",
    "boardingPositionRef",
    "trainElements"
})
@XmlSeeAlso({
    PassengerStopAssignment.class,
    DynamicStopAssignment_VersionStructure.class
})
public class PassengerStopAssignment_VersionStructure
    extends StopAssignment_VersionStructure
{

    @XmlElement(name = "StopPlaceRef")
    protected StopPlaceRefStructure stopPlaceRef;
    @XmlElement(name = "QuayRef")
    protected QuayRefStructure quayRef;
    @XmlElement(name = "BoardingPositionRef")
    protected BoardingPositionRefStructure boardingPositionRef;
    protected PassengerStopAssignment_VersionStructure.TrainElements trainElements;

    /**
     * Gets the value of the stopPlaceRef property.
     * 
     * @return
     *     possible object is
     *     {@link StopPlaceRefStructure }
     *     
     */
    public StopPlaceRefStructure getStopPlaceRef() {
        return stopPlaceRef;
    }

    /**
     * Sets the value of the stopPlaceRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link StopPlaceRefStructure }
     *     
     */
    public void setStopPlaceRef(StopPlaceRefStructure value) {
        this.stopPlaceRef = value;
    }

    /**
     * Gets the value of the quayRef property.
     * 
     * @return
     *     possible object is
     *     {@link QuayRefStructure }
     *     
     */
    public QuayRefStructure getQuayRef() {
        return quayRef;
    }

    /**
     * Sets the value of the quayRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuayRefStructure }
     *     
     */
    public void setQuayRef(QuayRefStructure value) {
        this.quayRef = value;
    }

    /**
     * Gets the value of the boardingPositionRef property.
     * 
     * @return
     *     possible object is
     *     {@link BoardingPositionRefStructure }
     *     
     */
    public BoardingPositionRefStructure getBoardingPositionRef() {
        return boardingPositionRef;
    }

    /**
     * Sets the value of the boardingPositionRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoardingPositionRefStructure }
     *     
     */
    public void setBoardingPositionRef(BoardingPositionRefStructure value) {
        this.boardingPositionRef = value;
    }

    /**
     * Gets the value of the trainElements property.
     * 
     * @return
     *     possible object is
     *     {@link PassengerStopAssignment_VersionStructure.TrainElements }
     *     
     */
    public PassengerStopAssignment_VersionStructure.TrainElements getTrainElements() {
        return trainElements;
    }

    /**
     * Sets the value of the trainElements property.
     * 
     * @param value
     *     allowed object is
     *     {@link PassengerStopAssignment_VersionStructure.TrainElements }
     *     
     */
    public void setTrainElements(PassengerStopAssignment_VersionStructure.TrainElements value) {
        this.trainElements = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.netex.org.uk/netex}trainStopAssignments_RelStructure">
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TrainElements
        extends TrainStopAssignments_RelStructure
    {


    }

}
