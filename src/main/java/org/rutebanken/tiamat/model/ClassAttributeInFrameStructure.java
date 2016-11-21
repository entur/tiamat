package org.rutebanken.tiamat.model;

public class ClassAttributeInFrameStructure {

    protected String type;
    protected MandatoryEnumeration mandatory;
    protected String name;

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public MandatoryEnumeration getMandatory() {
        return mandatory;
    }

    public void setMandatory(MandatoryEnumeration value) {
        this.mandatory = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

}
