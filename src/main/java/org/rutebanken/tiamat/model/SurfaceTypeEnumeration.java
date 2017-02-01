package org.rutebanken.tiamat.model;

public enum SurfaceTypeEnumeration {

    ASPHALT("asphalt"),
    BRICKS("bricks"),
    COBBLES("cobbles"),
    EARTH("earth"),
    GRASS("grass"),
    LOOSE_SURFACE("looseSurface"),
    PAVING_STONES("pavingStones"),
    ROUGH_SURFACE("roughSurface"),
    SMOOTH("smooth"),
    OTHER("other");
    private final String value;

    SurfaceTypeEnumeration(String v) {
        value = v;
    }

    public static SurfaceTypeEnumeration fromValue(String v) {
        for (SurfaceTypeEnumeration c : SurfaceTypeEnumeration.values()) {
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
