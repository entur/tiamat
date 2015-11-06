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
 * <p>Java class for PurchaseMomentEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PurchaseMomentEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="onReservation"/>
 *     &lt;enumeration value="beforeBoarding"/>
 *     &lt;enumeration value="onBoarding"/>
 *     &lt;enumeration value="afterBoarding"/>
 *     &lt;enumeration value="onCheckOut"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PurchaseMomentEnumeration")
@XmlEnum
public enum PurchaseMomentEnumeration {

    @XmlEnumValue("onReservation")
    ON_RESERVATION("onReservation"),
    @XmlEnumValue("beforeBoarding")
    BEFORE_BOARDING("beforeBoarding"),
    @XmlEnumValue("onBoarding")
    ON_BOARDING("onBoarding"),
    @XmlEnumValue("afterBoarding")
    AFTER_BOARDING("afterBoarding"),
    @XmlEnumValue("onCheckOut")
    ON_CHECK_OUT("onCheckOut"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    PurchaseMomentEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PurchaseMomentEnumeration fromValue(String v) {
        for (PurchaseMomentEnumeration c: PurchaseMomentEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
