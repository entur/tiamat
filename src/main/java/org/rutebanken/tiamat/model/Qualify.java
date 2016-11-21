package org.rutebanken.tiamat.model;

public class Qualify {

    protected MultilingualStringEntity qualifierName;
    protected TopographicPlaceRefStructure topographicPlaceRef;

    public MultilingualStringEntity getQualifierName() {
        return qualifierName;
    }

    public void setQualifierName(MultilingualStringEntity value) {
        this.qualifierName = value;
    }

    public TopographicPlaceRefStructure getTopographicPlaceRef() {
        return topographicPlaceRef;
    }

    public void setTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.topographicPlaceRef = value;
    }

}
