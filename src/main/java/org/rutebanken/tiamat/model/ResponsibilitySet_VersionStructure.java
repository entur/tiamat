

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ResponsibilitySet_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected ResponsibilityRoleAssignments_RelStructure roles;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public ResponsibilityRoleAssignments_RelStructure getRoles() {
        return roles;
    }

    public void setRoles(ResponsibilityRoleAssignments_RelStructure value) {
        this.roles = value;
    }

}
