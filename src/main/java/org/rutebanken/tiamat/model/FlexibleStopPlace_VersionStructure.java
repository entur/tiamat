package org.rutebanken.tiamat.model;

public class FlexibleStopPlace_VersionStructure
        extends Place {

    protected MultilingualStringEntity nameSuffix;
    protected AlternativeNames_RelStructure alternativeNames;
    protected VehicleModeEnumeration transportMode;
    protected String publicCode;
    protected Areas areas;

    public MultilingualStringEntity getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(MultilingualStringEntity value) {
        this.nameSuffix = value;
    }

    public AlternativeNames_RelStructure getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(AlternativeNames_RelStructure value) {
        this.alternativeNames = value;
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

}
