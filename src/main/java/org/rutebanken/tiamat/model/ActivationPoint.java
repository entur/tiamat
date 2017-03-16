package org.rutebanken.tiamat.model;

public class ActivationPoint extends Point {

    protected String activationPointNumber;
    protected MultilingualStringEntity shortName;
    protected PrivateCodeStructure privateCode;
    protected TypeOfActivationRefStructure typeOfActivationRef;

    public String getActivationPointNumber() {
        return activationPointNumber;
    }

    public void setActivationPointNumber(String value) {
        this.activationPointNumber = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public TypeOfActivationRefStructure getTypeOfActivationRef() {
        return typeOfActivationRef;
    }

    public void setTypeOfActivationRef(TypeOfActivationRefStructure value) {
        this.typeOfActivationRef = value;
    }

}
