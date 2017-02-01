package org.rutebanken.tiamat.model;

public enum PointOfInterestSpaceTypeEnumeration {

    ARENA("arena"),
    ARCHERY_ARENA("archeryArena"),
    ATHLETE_AREA("athleteArea"),
    AUDITORIUM("auditorium"),
    CHANGING_ROOM("changingRoom"),
    COURT("court"),
    DOWNHILL_SKIING_COURSE("downhillSkiingCourse"),
    FREESTYLE_SKIING_COURSE("freestyleSkiingCourse"),
    SKIBOARDING_AREA("skiboardingArea"),
    GATES("gates"),
    GREEN_ROOM("greenRoom"),
    HOSPITALITY_ZONE("hospitalityZone"),
    ICE_RINK("iceRink"),
    ORCHESTRAL_PIT("orchestralPit"),
    PLAYING_FIELD("playingField"),
    PODIUM("podium"),
    POOL("pool"),
    DIVING_POOL("divingPool"),
    PRESS_AREA("pressArea"),
    QUEUING_AREA_FOR_ENTRANCE("queuingAreaForEntrance"),
    RIDING_AREA("ridingArea"),
    ROWING_AREA("rowingArea"),
    SECURITY_SCREENING_AREA("securityScreeningArea"),
    SLED_RUN("sledRun"),
    SPECTATOR_TERRACE("spectatorTerrace"),
    SPECTATOR_SEATING("spectatorSeating"),
    SPECTATOR_STANDING_AREA("spectatorStandingArea"),
    SPORTS_AREA("sportsArea"),
    STABLING("stabling"),
    STAGE("stage"),
    RING("ring"),
    TICKETING("ticketing"),
    TRACK("track"),
    TRACKSIDE("trackside"),
    VELODROME("velodrome"),
    WARM_UP_AREA("warmUpArea"),
    WATERSIDE("waterside"),
    UNDEFINED("undefined"),
    OTHER("other");
    private final String value;

    PointOfInterestSpaceTypeEnumeration(String v) {
        value = v;
    }

    public static PointOfInterestSpaceTypeEnumeration fromValue(String v) {
        for (PointOfInterestSpaceTypeEnumeration c : PointOfInterestSpaceTypeEnumeration.values()) {
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
