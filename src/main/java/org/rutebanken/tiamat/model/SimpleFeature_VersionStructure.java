

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class SimpleFeature_VersionStructure
    extends GroupOfPoints_VersionStructure
{

    protected JAXBElement<? extends ZoneRefStructure> zoneRef;

    public JAXBElement<? extends ZoneRefStructure> getZoneRef() {
        return zoneRef;
    }

    public void setZoneRef(JAXBElement<? extends ZoneRefStructure> value) {
        this.zoneRef = value;
    }

}
