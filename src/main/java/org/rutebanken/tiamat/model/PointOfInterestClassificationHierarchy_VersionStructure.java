

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class PointOfInterestClassificationHierarchy_VersionStructure
    extends GroupOfEntities_VersionStructure
{

    protected PointOfInterestClassificationHierarchyMembers_RelStructure members;

    public PointOfInterestClassificationHierarchyMembers_RelStructure getMembers() {
        return members;
    }

    public void setMembers(PointOfInterestClassificationHierarchyMembers_RelStructure value) {
        this.members = value;
    }

}
