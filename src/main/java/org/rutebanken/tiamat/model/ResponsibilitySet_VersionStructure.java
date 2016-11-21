package org.rutebanken.tiamat.model;

public class ResponsibilitySet_VersionStructure
        extends DataManagedObjectStructure {

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
