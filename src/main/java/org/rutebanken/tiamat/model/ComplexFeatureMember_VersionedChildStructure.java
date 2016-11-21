package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class ComplexFeatureMember_VersionedChildStructure
        extends AbstractGroupMember_VersionedChildStructure {

    protected ComplexFeatureRefStructure complexFeatureRef;
    protected SimpleFeatureRefStructure simpleFeatureRef;
    protected JAXBElement<? extends VersionOfObjectRefStructure> versionOfObjectRef;

    public ComplexFeatureRefStructure getComplexFeatureRef() {
        return complexFeatureRef;
    }

    public void setComplexFeatureRef(ComplexFeatureRefStructure value) {
        this.complexFeatureRef = value;
    }

    public SimpleFeatureRefStructure getSimpleFeatureRef() {
        return simpleFeatureRef;
    }

    public void setSimpleFeatureRef(SimpleFeatureRefStructure value) {
        this.simpleFeatureRef = value;
    }

    public JAXBElement<? extends VersionOfObjectRefStructure> getVersionOfObjectRef() {
        return versionOfObjectRef;
    }

    public void setVersionOfObjectRef(JAXBElement<? extends VersionOfObjectRefStructure> value) {
        this.versionOfObjectRef = value;
    }

}
