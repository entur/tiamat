

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum AccessibilityInfoFacilityEnumeration {

    AUDIO_INFORMATION("audioInformation"),
    AUDIO_FOR_HEARING_IMPAIRED("audioForHearingImpaired"),
    VISUAL_DISPLAYS("visualDisplays"),
    DISPLAYS_FOR_VISUALLY_IMPAIRED("displaysForVisuallyImpaired"),
    LARGE_PRINT_TIMETABLES("largePrintTimetables"),
    OTHER("other");
    private final String value;

    AccessibilityInfoFacilityEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccessibilityInfoFacilityEnumeration fromValue(String v) {
        for (AccessibilityInfoFacilityEnumeration c: AccessibilityInfoFacilityEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
