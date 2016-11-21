

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum CyclesOnServiceEnumeration {

    NOT_ALLOWED("notAllowed"),
    ONLY_FOLDING_ALLOWED("onlyFoldingAllowed"),
    ALLOWED_SUBJECT_TO_RESTRICTIONS("allowedSubjectToRestrictions"),
    MUST_BOOK("mustBook"),
    ALLOWED_AT_ALL_TIMES("allowedAtAllTimes");
    private final String value;

    CyclesOnServiceEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CyclesOnServiceEnumeration fromValue(String v) {
        for (CyclesOnServiceEnumeration c: CyclesOnServiceEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
