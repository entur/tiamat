package org.rutebanken.tiamat.model;

public class GroupOfTimebands_VersionedChildStructure
        extends GroupOfEntities_VersionStructure {

    protected TimebandRefs_RelStructure timebands;

    public TimebandRefs_RelStructure getTimebands() {
        return timebands;
    }

    public void setTimebands(TimebandRefs_RelStructure value) {
        this.timebands = value;
    }

}
