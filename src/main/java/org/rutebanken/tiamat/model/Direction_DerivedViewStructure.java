package org.rutebanken.tiamat.model;

public class Direction_DerivedViewStructure
        extends DerivedViewStructure {

    protected DirectionRefStructure directionRef;
    protected MultilingualStringEntity name;

    public DirectionRefStructure getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(DirectionRefStructure value) {
        this.directionRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

}
