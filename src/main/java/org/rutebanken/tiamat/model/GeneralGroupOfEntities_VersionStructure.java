package org.rutebanken.tiamat.model;

public class GeneralGroupOfEntities_VersionStructure
        extends GroupOfEntities_VersionStructure {

    protected ObjectRefs_RelStructure members;
    protected String nameOfMemberClass;

    public ObjectRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(ObjectRefs_RelStructure value) {
        this.members = value;
    }

    public String getNameOfMemberClass() {
        return nameOfMemberClass;
    }

    public void setNameOfMemberClass(String value) {
        this.nameOfMemberClass = value;
    }

}
