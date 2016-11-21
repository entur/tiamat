

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ComplexFeature_VersionStructure
    extends GroupOfPoints_VersionStructure
{

    protected GroupOfEntitiesRef groupOfEntitiesRef;
    protected ComplexFeatureMembers_RelStructure featureMembers;

    public GroupOfEntitiesRef getGroupOfEntitiesRef() {
        return groupOfEntitiesRef;
    }

    public void setGroupOfEntitiesRef(GroupOfEntitiesRef value) {
        this.groupOfEntitiesRef = value;
    }

    public ComplexFeatureMembers_RelStructure getFeatureMembers() {
        return featureMembers;
    }

    public void setFeatureMembers(ComplexFeatureMembers_RelStructure value) {
        this.featureMembers = value;
    }

}
