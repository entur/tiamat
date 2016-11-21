package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

public abstract class Place_VersionStructure
        extends Zone_VersionStructure {

    protected TypeOfPlaceRefs_RelStructure placeTypes;

    public Place_VersionStructure() {
    }

    public Place_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public TypeOfPlaceRefs_RelStructure getPlaceTypes() {
        return placeTypes;
    }

    public void setPlaceTypes(TypeOfPlaceRefs_RelStructure value) {
        this.placeTypes = value;
    }

}
