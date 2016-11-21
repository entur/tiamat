package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;


@MappedSuperclass
public class VersionedChildStructure
        extends EntityInVersionStructure {

    @Transient
    protected ExtensionsStructure extensions;

    public ExtensionsStructure getExtensions() {
        return extensions;
    }

    public void setExtensions(ExtensionsStructure value) {
        this.extensions = value;
    }

}
