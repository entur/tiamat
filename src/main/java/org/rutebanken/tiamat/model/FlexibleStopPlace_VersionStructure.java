package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;

public class FlexibleStopPlace_VersionStructure
        extends Place {

    protected MultilingualStringEntity nameSuffix;

    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    protected VehicleModeEnumeration transportMode;
    protected String publicCode;
    protected Areas areas;

    public MultilingualStringEntity getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(MultilingualStringEntity value) {
        this.nameSuffix = value;
    }

    public VehicleModeEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(VehicleModeEnumeration value) {
        this.transportMode = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public Areas getAreas() {
        return areas;
    }

    public void setAreas(Areas value) {
        this.areas = value;
    }

    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}
