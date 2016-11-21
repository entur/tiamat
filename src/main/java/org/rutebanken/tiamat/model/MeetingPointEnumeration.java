package org.rutebanken.tiamat.model;

public enum MeetingPointEnumeration {

    MEETING_POINT("meetingPoint"),
    GROUP_MEETING("groupMeeting"),
    SCHOOL_MEETING_POINT("schoolMeetingPoint"),
    OTHER("other");
    private final String value;

    MeetingPointEnumeration(String v) {
        value = v;
    }

    public static MeetingPointEnumeration fromValue(String v) {
        for (MeetingPointEnumeration c : MeetingPointEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
