package org.rutebanken.tiamat.model;

public class GroupMember_VersionedChildStructure
        extends AbstractGroupMember_VersionedChildStructure {

    protected VersionOfObjectRefStructure groupRef;
    protected VersionOfObjectRefStructure memberObjectRef;

    public VersionOfObjectRefStructure getGroupRef() {
        return groupRef;
    }

    public void setGroupRef(VersionOfObjectRefStructure value) {
        this.groupRef = value;
    }

    public VersionOfObjectRefStructure getMemberObjectRef() {
        return memberObjectRef;
    }

    public void setMemberObjectRef(VersionOfObjectRefStructure value) {
        this.memberObjectRef = value;
    }

}
