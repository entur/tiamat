package org.rutebanken.tiamat.model;

import javax.persistence.Embedded;


public class PathLinkEndStructure {

    protected PlaceRefStructure placeRef;

    @Embedded
    protected LevelRefStructure levelRef;

    @Embedded
    protected EntranceRefStructure entranceRef;


    public PlaceRefStructure getPlaceRef() {
        return placeRef;
    }


    public void setPlaceRef(PlaceRefStructure value) {
        this.placeRef = value;
    }


    public LevelRefStructure getLevelRef() {
        return levelRef;
    }


    public void setLevelRef(LevelRefStructure value) {
        this.levelRef = value;
    }


    public EntranceRefStructure getEntranceRef() {
        return entranceRef;
    }


    public void setEntranceRef(EntranceRefStructure value) {
        this.entranceRef = value;
    }

}
