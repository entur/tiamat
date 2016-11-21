

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public abstract class LocalService_VersionStructure
    extends Equipment_VersionStructure
{

    protected TypeOfServiceFeatureRefs_RelStructure typesOfServiceFeature;

    public TypeOfServiceFeatureRefs_RelStructure getTypesOfServiceFeature() {
        return typesOfServiceFeature;
    }

    public void setTypesOfServiceFeature(TypeOfServiceFeatureRefs_RelStructure value) {
        this.typesOfServiceFeature = value;
    }

}
