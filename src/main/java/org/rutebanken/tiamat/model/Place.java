package org.rutebanken.tiamat.model;

public abstract class Place
        extends Zone_VersionStructure {

    protected TypeOfPlaceRefs_RelStructure placeTypes;

    public Place() {
    }

    public Place(EmbeddableMultilingualString name) {
        super(name);
    }

    public TypeOfPlaceRefs_RelStructure getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(TypeOfPlaceRefs_RelStructure value) {
        this.placeTypes = value;
    }

}
