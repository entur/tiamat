package org.rutebanken.tiamat.model;

public enum FlexibleLineTypeEnumeration {

    CORRIDOR_SERVICE("corridorService"),
    MAIN_ROUTE_WITH_FLEXIBLE_ENDS("mainRouteWithFlexibleEnds"),
    FLEXIBLE_AREAS_ONLY("flexibleAreasOnly"),
    HAIL_AND_RIDE_SECTIONS("hailAndRideSections"),
    FIXED_STOP_AREA_WIDE("fixedStopAreaWide"),
    FREE_AREA_AREA_WIDE("freeAreaAreaWide"),
    MIXED_FLEXIBLE("mixedFlexible"),
    MIXED_FLEXIBLE_AND_FIXED("mixedFlexibleAndFixed"),
    FIXED("fixed"),
    OTHER("other");
    private final String value;

    FlexibleLineTypeEnumeration(String v) {
        value = v;
    }

    public static FlexibleLineTypeEnumeration fromValue(String v) {
        for (FlexibleLineTypeEnumeration c : FlexibleLineTypeEnumeration.values()) {
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
