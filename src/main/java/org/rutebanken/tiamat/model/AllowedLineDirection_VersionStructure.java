package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class AllowedLineDirection_VersionStructure
        extends DataManagedObjectStructure {

    protected JAXBElement<? extends LineRefStructure> lineRef;
    protected DirectionRefStructure directionRef;

    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    public DirectionRefStructure getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(DirectionRefStructure value) {
        this.directionRef = value;
    }

}
