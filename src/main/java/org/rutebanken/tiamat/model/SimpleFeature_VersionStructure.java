package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class SimpleFeature_VersionStructure
        extends GroupOfPoints_VersionStructure {

    protected JAXBElement<? extends ZoneRefStructure> zoneRef;

    public JAXBElement<? extends ZoneRefStructure> getZoneRef() {
        return zoneRef;
    }

    public void setZoneRef(JAXBElement<? extends ZoneRefStructure> value) {
        this.zoneRef = value;
    }

}
