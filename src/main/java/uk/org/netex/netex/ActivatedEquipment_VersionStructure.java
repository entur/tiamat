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
 * Type for an ACTIVATED EQUIPMENT.
 * 
 * <p>Java class for ActivatedEquipment_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ActivatedEquipment_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}Equipment_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}ActivatedEquipmentGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivatedEquipment_VersionStructure", propOrder = {
    "trafficControlPointRef",
    "typeOfActivationRef",
    "assignments"
})
@XmlSeeAlso({
    ActivatedEquipment.class
})
public class ActivatedEquipment_VersionStructure
    extends Equipment_VersionStructure
{

    @XmlElement(name = "TrafficControlPointRef")
    protected TrafficControlPointRefStructure trafficControlPointRef;
    @XmlElement(name = "TypeOfActivationRef")
    protected TypeOfActivationRefStructure typeOfActivationRef;
    protected ActivationAssignments_RelStructure assignments;

    /**
     * Gets the value of the trafficControlPointRef property.
     * 
     * @return
     *     possible object is
     *     {@link TrafficControlPointRefStructure }
     *     
     */
    public TrafficControlPointRefStructure getTrafficControlPointRef() {
        return trafficControlPointRef;
    }

    /**
     * Sets the value of the trafficControlPointRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrafficControlPointRefStructure }
     *     
     */
    public void setTrafficControlPointRef(TrafficControlPointRefStructure value) {
        this.trafficControlPointRef = value;
    }

    /**
     * Gets the value of the typeOfActivationRef property.
     * 
     * @return
     *     possible object is
     *     {@link TypeOfActivationRefStructure }
     *     
     */
    public TypeOfActivationRefStructure getTypeOfActivationRef() {
        return typeOfActivationRef;
    }

    /**
     * Sets the value of the typeOfActivationRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOfActivationRefStructure }
     *     
     */
    public void setTypeOfActivationRef(TypeOfActivationRefStructure value) {
        this.typeOfActivationRef = value;
    }

    /**
     * Gets the value of the assignments property.
     * 
     * @return
     *     possible object is
     *     {@link ActivationAssignments_RelStructure }
     *     
     */
    public ActivationAssignments_RelStructure getAssignments() {
        return assignments;
    }

    /**
     * Sets the value of the assignments property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivationAssignments_RelStructure }
     *     
     */
    public void setAssignments(ActivationAssignments_RelStructure value) {
        this.assignments = value;
    }

}
