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
 * <p>Java class for TelecabinSubmodeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TelecabinSubmodeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="unknown"/>
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="telecabin"/>
 *     &lt;enumeration value="cableCar"/>
 *     &lt;enumeration value="lift"/>
 *     &lt;enumeration value="chairLift"/>
 *     &lt;enumeration value="dragLift"/>
 *     &lt;enumeration value="telecabinLink"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TelecabinSubmodeEnumeration")
@XmlEnum
public enum TelecabinSubmodeEnumeration {

    @XmlEnumValue("unknown")
    UNKNOWN("unknown"),
    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("telecabin")
    TELECABIN("telecabin"),
    @XmlEnumValue("cableCar")
    CABLE_CAR("cableCar"),
    @XmlEnumValue("lift")
    LIFT("lift"),
    @XmlEnumValue("chairLift")
    CHAIR_LIFT("chairLift"),
    @XmlEnumValue("dragLift")
    DRAG_LIFT("dragLift"),
    @XmlEnumValue("telecabinLink")
    TELECABIN_LINK("telecabinLink");
    private final String value;

    TelecabinSubmodeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TelecabinSubmodeEnumeration fromValue(String v) {
        for (TelecabinSubmodeEnumeration c: TelecabinSubmodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
