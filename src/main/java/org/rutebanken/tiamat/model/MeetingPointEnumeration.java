

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


public enum MeetingPointEnumeration {

    MEETING_POINT("meetingPoint"),
    GROUP_MEETING("groupMeeting"),
    SCHOOL_MEETING_POINT("schoolMeetingPoint"),
    OTHER("other");
    private final String value;

    MeetingPointEnumeration(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MeetingPointEnumeration fromValue(String v) {
        for (MeetingPointEnumeration c: MeetingPointEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
