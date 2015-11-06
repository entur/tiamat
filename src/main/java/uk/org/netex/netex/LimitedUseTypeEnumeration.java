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
 * <p>Java class for LimitedUseTypeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LimitedUseTypeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="interchangeOnly"/>
 *     &lt;enumeration value="noDirectRoadAccess"/>
 *     &lt;enumeration value="longWalkToAccess"/>
 *     &lt;enumeration value="isolated"/>
 *     &lt;enumeration value="limitedService"/>
 *     &lt;enumeration value="other"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LimitedUseTypeEnumeration")
@XmlEnum
public enum LimitedUseTypeEnumeration {


    /**
     * Stop may only be used for interchange, not for entrance or exit.
     * 
     */
    @XmlEnumValue("interchangeOnly")
    INTERCHANGE_ONLY("interchangeOnly"),

    /**
     * Stop may not be reached from Road by a paved path.
     * 
     */
    @XmlEnumValue("noDirectRoadAccess")
    NO_DIRECT_ROAD_ACCESS("noDirectRoadAccess"),

    /**
     * Stop may only be accessed by a long (>200m) walk from road.
     * 
     */
    @XmlEnumValue("longWalkToAccess")
    LONG_WALK_TO_ACCESS("longWalkToAccess"),

    /**
     * Stop is an island or ferry stop that does not connect to rad network.
     * 
     */
    @XmlEnumValue("isolated")
    ISOLATED("isolated"),

    /**
     * Stop has a very limited service.
     * 
     */
    @XmlEnumValue("limitedService")
    LIMITED_SERVICE("limitedService"),
    @XmlEnumValue("other")
    OTHER("other");
    private final String value;

    LimitedUseTypeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LimitedUseTypeEnumeration fromValue(String v) {
        for (LimitedUseTypeEnumeration c: LimitedUseTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
