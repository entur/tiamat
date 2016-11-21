

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class Line_DerivedViewStructure
    extends DerivedViewStructure
{

    protected JAXBElement<? extends LineRefStructure> lineRef;
    protected String publicCode;
    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected AllVehicleModesOfTransportEnumeration transportMode;
    protected TransportSubmodeStructure transportSubmode;
    protected OperatorRefStructure operatorRef;
    protected TypeOfLineRefStructure typeOfLineRef;

    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public AllVehicleModesOfTransportEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(AllVehicleModesOfTransportEnumeration value) {
        this.transportMode = value;
    }

    public TransportSubmodeStructure getTransportSubmode() {
        return transportSubmode;
    }

    public void setTransportSubmode(TransportSubmodeStructure value) {
        this.transportSubmode = value;
    }

    public OperatorRefStructure getOperatorRef() {
        return operatorRef;
    }

    public void setOperatorRef(OperatorRefStructure value) {
        this.operatorRef = value;
    }

    public TypeOfLineRefStructure getTypeOfLineRef() {
        return typeOfLineRef;
    }

    public void setTypeOfLineRef(TypeOfLineRefStructure value) {
        this.typeOfLineRef = value;
    }

}
