

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "usedIn",
public class CommonSection_VersionStructure
    extends GroupOfEntities_VersionStructure
{

    protected CommonSectionSequenceMembers_RelStructure usedIn;
    protected CommonSectionPointMembers_RelStructure members;

    public CommonSectionSequenceMembers_RelStructure getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(CommonSectionSequenceMembers_RelStructure value) {
        this.usedIn = value;
    }

    public CommonSectionPointMembers_RelStructure getMembers() {
        return members;
    }

    public void setMembers(CommonSectionPointMembers_RelStructure value) {
        this.members = value;
    }

}
