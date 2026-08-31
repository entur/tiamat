package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.annotation.Nonnull;
import org.rutebanken.tiamat.model.EntityInVersionStructure;

public interface SearchKeyService {
    @Nonnull
    String generateSearchKeyJSON(EntityInVersionStructure entity);
}
