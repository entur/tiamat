package org.rutebanken.tiamat.ext.fintraffic.api;

import org.rutebanken.tiamat.model.EntityInVersionStructure;

public interface SearchKeyService {
    String generateSearchKeyJSON(EntityInVersionStructure entity);
}
