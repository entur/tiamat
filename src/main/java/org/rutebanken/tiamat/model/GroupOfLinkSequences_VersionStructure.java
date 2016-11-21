

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class GroupOfLinkSequences_VersionStructure
    extends GroupOfEntities_VersionStructure
{

    protected LinkSequenceRefs_RelStructure members;

    public LinkSequenceRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(LinkSequenceRefs_RelStructure value) {
        this.members = value;
    }

}
