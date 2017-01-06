package org.rutebanken.tiamat.model;

public class ValueSet_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected TypesOfValueStructure values;
    protected String classOfValues;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public TypesOfValueStructure getValues() {
        return values;
    }

    public void setValues(TypesOfValueStructure value) {
        this.values = value;
    }

    public String getClassOfValues() {
        return classOfValues;
    }

    public void setClassOfValues(String value) {
    }

}
