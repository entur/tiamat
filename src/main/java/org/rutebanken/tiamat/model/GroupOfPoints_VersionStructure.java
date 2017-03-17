package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class GroupOfPoints_VersionStructure
        extends GroupOfEntities_VersionStructure {

    @Transient
    protected PointRefs_RelStructure members;

    public GroupOfPoints_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public GroupOfPoints_VersionStructure() {
    }

    public PointRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(PointRefs_RelStructure value) {
        this.members = value;
    }

}
