package org.rutebanken.tiamat.model;

public class AlternativeQuayDescriptor_VersionedChildStructure
        extends AlternativeName_VersionedChildStructure {

    protected MultilingualStringEntity crossRoad;
    protected MultilingualStringEntity landmark;

    public MultilingualStringEntity getCrossRoad() {
        return crossRoad;
    }

    public void setCrossRoad(MultilingualStringEntity value) {
        this.crossRoad = value;
    }

    public MultilingualStringEntity getLandmark() {
        return landmark;
    }

    public void setLandmark(MultilingualStringEntity value) {
        this.landmark = value;
    }

}
