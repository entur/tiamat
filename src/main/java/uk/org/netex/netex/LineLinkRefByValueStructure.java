//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a reference to a LINE LINK BY VALUE.
 * 
 * <p>Java class for LineLinkRefByValueStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LineLinkRefByValueStructure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.netex.org.uk/netex}LinkRefByValueStructure">
 *       &lt;attribute name="fromPointRef" use="required" type="{http://www.netex.org.uk/netex}PointIdType" />
 *       &lt;attribute name="toPointRef" use="required" type="{http://www.netex.org.uk/netex}PointIdType" />
 *       &lt;attribute name="nameOfPointRefClass" type="{http://www.netex.org.uk/netex}NameOfClass" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineLinkRefByValueStructure")
public class LineLinkRefByValueStructure
    extends LinkRefByValueStructure
{


}
