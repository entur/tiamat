package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class CrewBase_VersionStructure
        extends GroupOfEntities_VersionStructure {

    protected JAXBElement<? extends ReliefPointRefStructure> reliefPointRef;
    protected GarageRefs_RelStructure garages;

    public JAXBElement<? extends ReliefPointRefStructure> getReliefPointRef() {
        return reliefPointRef;
    }

    public void setReliefPointRef(JAXBElement<? extends ReliefPointRefStructure> value) {
        this.reliefPointRef = value;
    }

    public GarageRefs_RelStructure getGarages() {
        return garages;
    }

    public void setGarages(GarageRefs_RelStructure value) {
        this.garages = value;
    }

}
