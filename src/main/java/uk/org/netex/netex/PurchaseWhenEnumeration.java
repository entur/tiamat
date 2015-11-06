//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PurchaseWhenEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PurchaseWhenEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="timeOfTravelOnly"/>
 *     &lt;enumeration value="dayOfTravelOnly"/>
 *     &lt;enumeration value="untilPreviousDay"/>
 *     &lt;enumeration value="advanceOnly"/>
 *     &lt;enumeration value="advanceAndDayOfTravel"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PurchaseWhenEnumeration")
@XmlEnum
public enum PurchaseWhenEnumeration {

    @XmlEnumValue("timeOfTravelOnly")
    TIME_OF_TRAVEL_ONLY("timeOfTravelOnly"),
    @XmlEnumValue("dayOfTravelOnly")
    DAY_OF_TRAVEL_ONLY("dayOfTravelOnly"),
    @XmlEnumValue("untilPreviousDay")
    UNTIL_PREVIOUS_DAY("untilPreviousDay"),
    @XmlEnumValue("advanceOnly")
    ADVANCE_ONLY("advanceOnly"),
    @XmlEnumValue("advanceAndDayOfTravel")
    ADVANCE_AND_DAY_OF_TRAVEL("advanceAndDayOfTravel"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    PurchaseWhenEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PurchaseWhenEnumeration fromValue(String v) {
        for (PurchaseWhenEnumeration c: PurchaseWhenEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
