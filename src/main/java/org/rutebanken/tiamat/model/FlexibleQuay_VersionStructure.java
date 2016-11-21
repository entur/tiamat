

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "nameSuffix",
    "alternativeNames",
    "flexibleStopPlaceRef",
    "transportMode",
    "boardingUse",
    "alightingUse",
public class FlexibleQuay_VersionStructure
    extends Place_VersionStructure
{

    protected MultilingualStringEntity nameSuffix;
    protected AlternativeNames_RelStructure alternativeNames;
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

    public AlternativeNames_RelStructure getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(AlternativeNames_RelStructure value) {
        this.alternativeNames = value;
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

}
