

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class GroupOfPoints_VersionStructure
    extends GroupOfEntities_VersionStructure
{

    protected PointRefs_RelStructure members;

    public GroupOfPoints_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public GroupOfPoints_VersionStructure() {}

    public PointRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(PointRefs_RelStructure value) {
        this.members = value;
    }

}
