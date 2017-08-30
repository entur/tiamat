package org.rutebanken.tiamat.model;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

public class FlexibleQuay_VersionStructure
        extends Place {

    protected MultilingualStringEntity nameSuffix;

    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    protected FlexibleStopPlaceRefStructure flexibleStopPlaceRef;
    protected VehicleModeEnumeration transportMode;
    protected Boolean boardingUse;
    protected Boolean alightingUse;
    protected String publicCode;

    public MultilingualStringEntity getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(MultilingualStringEntity value) {
        this.nameSuffix = value;
    }

    public FlexibleStopPlaceRefStructure getFlexibleStopPlaceRef() {
        return flexibleStopPlaceRef;
    }

    public void setFlexibleStopPlaceRef(FlexibleStopPlaceRefStructure value) {
        this.flexibleStopPlaceRef = value;
    }

    public VehicleModeEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(VehicleModeEnumeration value) {
        this.transportMode = value;
    }

    public Boolean isBoardingUse() {
        return boardingUse;
    }

    public void setBoardingUse(Boolean value) {
        this.boardingUse = value;
    }

    public Boolean isAlightingUse() {
        return alightingUse;
    }

    public void setAlightingUse(Boolean value) {
        this.alightingUse = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}
