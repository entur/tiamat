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
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.netex.org.uk/netex}PassengerStopAssignment_DerivedViewStructure">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.netex.org.uk/netex}PassengerStopAssignmentRef" minOccurs="0"/>
 *         &lt;element ref="{http://www.netex.org.uk/netex}StopPlaceRef" minOccurs="0"/>
 *         &lt;element name="QuayRef" type="{http://www.netex.org.uk/netex}QuayRefStructure" minOccurs="0"/>
 *         &lt;element name="QuayName" type="{http://www.netex.org.uk/netex}MultilingualString" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.netex.org.uk/netex}PassengerStopAssignmentIdType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class PassengerStopAssignmentView
    extends PassengerStopAssignment_DerivedViewStructure
{


}
