//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a list of DESTINATION DISPLAY VARIANTs.
 * 
 * <p>Java class for destinationDisplayVariants_RelStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="destinationDisplayVariants_RelStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}strictContainmentAggregationStructure">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.netex.org.uk/netex}DestinationDisplayVariant" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "destinationDisplayVariants_RelStructure", propOrder = {
    "destinationDisplayVariant"
})
@XmlSeeAlso({
    uk.org.netex.netex.DestinationDisplay_VersionStructure.Variants.class
})
public class DestinationDisplayVariants_RelStructure
    extends StrictContainmentAggregationStructure
{

    @XmlElement(name = "DestinationDisplayVariant", required = true)
    protected List<DestinationDisplayVariant> destinationDisplayVariant;

    /**
     * Gets the value of the destinationDisplayVariant property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the destinationDisplayVariant property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDestinationDisplayVariant().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DestinationDisplayVariant }
     * 
     * 
     */
    public List<DestinationDisplayVariant> getDestinationDisplayVariant() {
        if (destinationDisplayVariant == null) {
            destinationDisplayVariant = new ArrayList<DestinationDisplayVariant>();
        }
        return this.destinationDisplayVariant;
    }

}
