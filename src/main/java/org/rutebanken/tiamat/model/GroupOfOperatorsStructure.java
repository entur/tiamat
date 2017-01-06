package org.rutebanken.tiamat.model;

public class GroupOfOperatorsStructure
        extends GroupOfEntities_VersionStructure {

    protected TransportOrganisationRefs_RelStructure members;

    public TransportOrganisationRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(TransportOrganisationRefs_RelStructure value) {
        this.members = value;
    }

}
