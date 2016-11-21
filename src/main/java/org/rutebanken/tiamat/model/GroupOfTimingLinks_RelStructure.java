

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class GroupOfTimingLinks_RelStructure
    extends GroupOfEntities_VersionStructure
{

    protected TimingLinkRefs_RelStructure members;

    public TimingLinkRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(TimingLinkRefs_RelStructure value) {
        this.members = value;
    }

}
