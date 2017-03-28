package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class GroupOfEntitiesRefStructure extends VersionOfObjectRefStructure {

    public GroupOfEntitiesRefStructure() {
        super();
    }

    public GroupOfEntitiesRefStructure(String ref, String version) {
        super(ref, version);
    }
}
