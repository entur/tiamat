package org.rutebanken.tiamat.model;

public class InfoLinkStructure {

    protected String value;
    protected TypeOfInfolinkEnumeration typeOfInfoLink;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TypeOfInfolinkEnumeration getTypeOfInfoLink() {
        return typeOfInfoLink;
    }

    public void setTypeOfInfoLink(TypeOfInfolinkEnumeration value) {
        this.typeOfInfoLink = value;
    }

}
