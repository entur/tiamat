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
 * Type for a VEHICLE POSTION ALIGNMENT.
 * 
 * <p>Java class for VehiclePositionAlignment_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VehiclePositionAlignment_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}VersionedChildStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}VehiclePositionAlignmentGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehiclePositionAlignment_VersionStructure", propOrder = {
    "vehicleStoppingPositionRef",
    "boardingPositionRef",
    "boardingPositionEntranceRef"
})
@XmlSeeAlso({
    VehiclePositionAlignment.class
})
public class VehiclePositionAlignment_VersionStructure
    extends VersionedChildStructure
{

    @XmlElement(name = "VehicleStoppingPositionRef")
    protected VehicleStoppingPositionRefStructure vehicleStoppingPositionRef;
    @XmlElement(name = "BoardingPositionRef")
    protected BoardingPositionRefStructure boardingPositionRef;
    @XmlElement(name = "BoardingPositionEntranceRef")
    protected StopPlaceEntranceRefStructure boardingPositionEntranceRef;

    /**
     * Gets the value of the vehicleStoppingPositionRef property.
     * 
     * @return
     *     possible object is
     *     {@link VehicleStoppingPositionRefStructure }
     *     
     */
    public VehicleStoppingPositionRefStructure getVehicleStoppingPositionRef() {
        return vehicleStoppingPositionRef;
    }

    /**
     * Sets the value of the vehicleStoppingPositionRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehicleStoppingPositionRefStructure }
     *     
     */
    public void setVehicleStoppingPositionRef(VehicleStoppingPositionRefStructure value) {
        this.vehicleStoppingPositionRef = value;
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
     * Gets the value of the boardingPositionEntranceRef property.
     * 
     * @return
     *     possible object is
     *     {@link StopPlaceEntranceRefStructure }
     *     
     */
    public StopPlaceEntranceRefStructure getBoardingPositionEntranceRef() {
        return boardingPositionEntranceRef;
    }

    /**
     * Sets the value of the boardingPositionEntranceRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link StopPlaceEntranceRefStructure }
     *     
     */
    public void setBoardingPositionEntranceRef(StopPlaceEntranceRefStructure value) {
        this.boardingPositionEntranceRef = value;
    }

}
