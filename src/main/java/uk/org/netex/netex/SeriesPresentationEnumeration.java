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
 * <p>Java class for SeriesPresentationEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SeriesPresentationEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="required"/>
 *     &lt;enumeration value="optionalLeft"/>
 *     &lt;enumeration value="optionalRight"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SeriesPresentationEnumeration")
@XmlEnum
public enum SeriesPresentationEnumeration {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("required")
    REQUIRED("required"),
    @XmlEnumValue("optionalLeft")
    OPTIONAL_LEFT("optionalLeft"),
    @XmlEnumValue("optionalRight")
    OPTIONAL_RIGHT("optionalRight");
    private final String value;

    SeriesPresentationEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SeriesPresentationEnumeration fromValue(String v) {
        for (SeriesPresentationEnumeration c: SeriesPresentationEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
