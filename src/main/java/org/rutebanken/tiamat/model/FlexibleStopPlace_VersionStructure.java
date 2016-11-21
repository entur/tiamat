

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
    "transportMode",
    "publicCode",
    "areas",
public class FlexibleStopPlace_VersionStructure
    extends Place_VersionStructure
{

    protected MultilingualStringEntity nameSuffix;
    protected AlternativeNames_RelStructure alternativeNames;
    protected VehicleModeEnumeration transportMode;
    protected String publicCode;
    protected Areas areas;
    protected LineRefs_RelStructure lines;

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

    public LineRefs_RelStructure getLines() {
        return lines;
    }

    public void setLines(LineRefs_RelStructure value) {
        this.lines = value;
    }

}
