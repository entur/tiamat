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
import javax.xml.bind.annotation.XmlType;


/**
 * Type for containment in frame of BORDER POINT.
 * 
 * <p>Java class for borderPointsInFrame_RelStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="borderPointsInFrame_RelStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}frameContainmentStructure">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.netex.org.uk/netex}BorderPoint" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "borderPointsInFrame_RelStructure", propOrder = {
    "borderPoint"
})
public class BorderPointsInFrame_RelStructure
    extends FrameContainmentStructure
{

    @XmlElement(name = "BorderPoint", required = true)
    protected List<BorderPoint> borderPoint;

    /**
     * Gets the value of the borderPoint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the borderPoint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBorderPoint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BorderPoint }
     * 
     * 
     */
    public List<BorderPoint> getBorderPoint() {
        if (borderPoint == null) {
            borderPoint = new ArrayList<BorderPoint>();
        }
        return this.borderPoint;
    }

}
