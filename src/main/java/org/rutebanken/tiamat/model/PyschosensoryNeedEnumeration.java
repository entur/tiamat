

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum PyschosensoryNeedEnumeration {

    VISUAL_IMPAIRMENT("visualImpairment"),
    AUDITORY_IMPAIRMENT("auditoryImpairment"),
    COGNITIVE_INPUT_IMPAIRMENT("cognitiveInputImpairment"),
    AVERSE_TO_LIFTS("averseToLifts"),
    AVERSE_TO_ESCALATORS("averseToEscalators"),
    AVERSE_TO_CONFINED_SPACES("averseToConfinedSpaces"),
    AVERSE_TO_CROWDS("averseToCrowds"),
    OTHER_PSYCHOSENSORY_NEED("otherPsychosensoryNeed");
    private final String value;

    PyschosensoryNeedEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PyschosensoryNeedEnumeration fromValue(String v) {
        for (PyschosensoryNeedEnumeration c: PyschosensoryNeedEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
