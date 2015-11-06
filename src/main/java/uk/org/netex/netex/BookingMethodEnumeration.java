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
 * <p>Java class for BookingMethodEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BookingMethodEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="callDriver"/>
 *     &lt;enumeration value="callOffice"/>
 *     &lt;enumeration value="online"/>
 *     &lt;enumeration value="other"/>
 *     &lt;enumeration value="phoneAtStop"/>
 *     &lt;enumeration value="text"/>
 *     &lt;enumeration value="none"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BookingMethodEnumeration")
@XmlEnum
public enum BookingMethodEnumeration {

    @XmlEnumValue("callDriver")
    CALL_DRIVER("callDriver"),
    @XmlEnumValue("callOffice")
    CALL_OFFICE("callOffice"),
    @XmlEnumValue("online")
    ONLINE("online"),
    @XmlEnumValue("other")
    OTHER("other"),
    @XmlEnumValue("phoneAtStop")
    PHONE_AT_STOP("phoneAtStop"),
    @XmlEnumValue("text")
    TEXT("text"),
    @XmlEnumValue("none")
    NONE("none");
    private final String value;

    BookingMethodEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BookingMethodEnumeration fromValue(String v) {
        for (BookingMethodEnumeration c: BookingMethodEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
