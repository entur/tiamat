

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


    "groupRef",
public class GroupMember_VersionedChildStructure
    extends AbstractGroupMember_VersionedChildStructure
{

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
