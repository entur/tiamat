package org.rutebanken.tiamat.model;

public class ClassInFrameStructure {

    protected ClassRefTypeEnumeration classRefType;
    protected String typeOfFrameRef;
    protected MandatoryEnumeration mandatory;
    protected Attributes attributes;
    protected Relationships relationships;
    protected String nameOfClass;

    public ClassRefTypeEnumeration getClassRefType() {
        return classRefType;
    }

    public void setClassRefType(ClassRefTypeEnumeration value) {
    }

    public String getTypeOfFrameRef() {
        return typeOfFrameRef;
    }

    public void setTypeOfFrameRef(String value) {
        this.typeOfFrameRef = value;
    }

    public MandatoryEnumeration getMandatory() {
        return mandatory;
    }

    public void setMandatory(MandatoryEnumeration value) {
        this.mandatory = value;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes value) {
        this.attributes = value;
    }

    public Relationships getRelationships() {
        return relationships;
    }

    public void setRelationships(Relationships value) {
        this.relationships = value;
    }

    public String getNameOfClass() {
        return nameOfClass;
    }

    public void setNameOfClass(String value) {
        this.nameOfClass = value;
    }

}
