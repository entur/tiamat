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
 * Type for a reference to a DISPLAY ASSIGNMENT.
 * 
 * <p>Java class for DisplayAssignmentRefStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DisplayAssignmentRefStructure">
 *   &lt;simpleContent>
 *     &lt;restriction base="&lt;http://www.netex.org.uk/netex>AssignmentRefStructure">
 *       &lt;attribute name="version" type="{http://www.netex.org.uk/netex}VersionIdType" />
 *       &lt;attribute name="order" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="ref" use="required" type="{http://www.netex.org.uk/netex}DisplayAssignmentIdType" />
 *     &lt;/restriction>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DisplayAssignmentRefStructure")
public class DisplayAssignmentRefStructure
    extends AssignmentRefStructure
{


}
