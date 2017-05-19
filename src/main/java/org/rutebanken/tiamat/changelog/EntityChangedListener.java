package org.rutebanken.tiamat.changelog;

import org.rutebanken.tiamat.model.EntityInVersionStructure;

public interface EntityChangedListener {

    void onChange(EntityInVersionStructure entity);
}
