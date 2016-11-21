

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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

    public String value() {
        return value;
    }

    public static SurfaceTypeEnumeration fromValue(String v) {
        for (SurfaceTypeEnumeration c: SurfaceTypeEnumeration.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
