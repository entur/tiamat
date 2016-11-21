

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class GroupOfTimebands_VersionedChildStructure
    extends GroupOfEntities_VersionStructure
{

    protected TimebandRefs_RelStructure timebands;

    public TimebandRefs_RelStructure getTimebands() {
        return timebands;
    }

    public void setTimebands(TimebandRefs_RelStructure value) {
        this.timebands = value;
    }

}
