

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class GroupOfLinks_VersionStructure
    extends GroupOfEntities_VersionStructure
{

    protected LinkRefs_RelStructure members;

    public LinkRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(LinkRefs_RelStructure value) {
        this.members = value;
    }

}
