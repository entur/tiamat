//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for reference to a SITE COMPONENT.
 * 
 * <p>Java class for SiteElementRefStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SiteElementRefStructure">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;http://www.netex.org.uk/netex>AddressablePlaceRefStructure">
 *       &lt;attribute name="ref" use="required" type="{http://www.netex.org.uk/netex}SiteElementIdType" />
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SiteElementRefStructure")
@XmlSeeAlso({
    SiteRefStructure.class,
    SiteComponentRefStructure.class
})
public class SiteElementRefStructure
    extends AddressablePlaceRefStructure
{


}
