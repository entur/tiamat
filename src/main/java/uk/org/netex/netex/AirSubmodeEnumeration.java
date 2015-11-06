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
 * <p>Java class for AirSubmodeEnumeration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AirSubmodeEnumeration">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="unknown"/>
 *     &lt;enumeration value="undefined"/>
 *     &lt;enumeration value="internationalFlight"/>
 *     &lt;enumeration value="domesticFlight"/>
 *     &lt;enumeration value="intercontinentalFlight"/>
 *     &lt;enumeration value="domesticScheduledFlight"/>
 *     &lt;enumeration value="shuttleFlight"/>
 *     &lt;enumeration value="intercontinentalCharterFlight"/>
 *     &lt;enumeration value="internationalCharterFlight"/>
 *     &lt;enumeration value="roundTripCharterFlight"/>
 *     &lt;enumeration value="sightseeingFlight"/>
 *     &lt;enumeration value="helicopterService"/>
 *     &lt;enumeration value="domesticCharterFlight"/>
 *     &lt;enumeration value="SchengenAreaFlight"/>
 *     &lt;enumeration value="airshipService"/>
 *     &lt;enumeration value="shortHaulInternationalFlight"/>
 *     &lt;enumeration value="canalBarge"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AirSubmodeEnumeration")
@XmlEnum
public enum AirSubmodeEnumeration {

    @XmlEnumValue("unknown")
    UNKNOWN("unknown"),
    @XmlEnumValue("undefined")
    UNDEFINED("undefined"),
    @XmlEnumValue("internationalFlight")
    INTERNATIONAL_FLIGHT("internationalFlight"),
    @XmlEnumValue("domesticFlight")
    DOMESTIC_FLIGHT("domesticFlight"),
    @XmlEnumValue("intercontinentalFlight")
    INTERCONTINENTAL_FLIGHT("intercontinentalFlight"),
    @XmlEnumValue("domesticScheduledFlight")
    DOMESTIC_SCHEDULED_FLIGHT("domesticScheduledFlight"),
    @XmlEnumValue("shuttleFlight")
    SHUTTLE_FLIGHT("shuttleFlight"),
    @XmlEnumValue("intercontinentalCharterFlight")
    INTERCONTINENTAL_CHARTER_FLIGHT("intercontinentalCharterFlight"),
    @XmlEnumValue("internationalCharterFlight")
    INTERNATIONAL_CHARTER_FLIGHT("internationalCharterFlight"),
    @XmlEnumValue("roundTripCharterFlight")
    ROUND_TRIP_CHARTER_FLIGHT("roundTripCharterFlight"),
    @XmlEnumValue("sightseeingFlight")
    SIGHTSEEING_FLIGHT("sightseeingFlight"),
    @XmlEnumValue("helicopterService")
    HELICOPTER_SERVICE("helicopterService"),
    @XmlEnumValue("domesticCharterFlight")
    DOMESTIC_CHARTER_FLIGHT("domesticCharterFlight"),
    @XmlEnumValue("SchengenAreaFlight")
    SCHENGEN_AREA_FLIGHT("SchengenAreaFlight"),
    @XmlEnumValue("airshipService")
    AIRSHIP_SERVICE("airshipService"),
    @XmlEnumValue("shortHaulInternationalFlight")
    SHORT_HAUL_INTERNATIONAL_FLIGHT("shortHaulInternationalFlight"),
    @XmlEnumValue("canalBarge")
    CANAL_BARGE("canalBarge");
    private final String value;

    AirSubmodeEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AirSubmodeEnumeration fromValue(String v) {
        for (AirSubmodeEnumeration c: AirSubmodeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
