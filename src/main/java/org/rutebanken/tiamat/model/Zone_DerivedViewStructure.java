

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class Zone_DerivedViewStructure
    extends DerivedViewStructure
{

    protected JAXBElement<? extends ZoneRefStructure> zoneRef;
    protected MultilingualStringEntity name;
    protected TypeOfZoneRefStructure typeOfZoneRef;

    public JAXBElement<? extends ZoneRefStructure> getZoneRef() {
        return zoneRef;
    }

    public void setZoneRef(JAXBElement<? extends ZoneRefStructure> value) {
        this.zoneRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public TypeOfZoneRefStructure getTypeOfZoneRef() {
        return typeOfZoneRef;
    }

    public void setTypeOfZoneRef(TypeOfZoneRefStructure value) {
        this.typeOfZoneRef = value;
    }

}
