package org.rutebanken.tiamat.model;

public class AliasStructure {

    protected PrivateCodeStructure privateCode;
    protected String identifierType;

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String value) {
        this.identifierType = value;
    }

}
