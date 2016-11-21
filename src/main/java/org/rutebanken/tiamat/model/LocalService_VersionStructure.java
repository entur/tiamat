package org.rutebanken.tiamat.model;

public abstract class LocalService_VersionStructure
        extends Equipment_VersionStructure {

    protected TypeOfServiceFeatureRefs_RelStructure typesOfServiceFeature;

    public TypeOfServiceFeatureRefs_RelStructure getTypesOfServiceFeature() {
        return typesOfServiceFeature;
    }

    public void setTypesOfServiceFeature(TypeOfServiceFeatureRefs_RelStructure value) {
        this.typesOfServiceFeature = value;
    }

}
