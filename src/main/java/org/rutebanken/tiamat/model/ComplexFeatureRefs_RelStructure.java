package org.rutebanken.tiamat.model;

public class ComplexFeatureRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected ComplexFeatureRefStructure complexFeatureRef;

    public ComplexFeatureRefStructure getComplexFeatureRef() {
        return complexFeatureRef;
    }

    public void setComplexFeatureRef(ComplexFeatureRefStructure value) {
        this.complexFeatureRef = value;
    }

}
