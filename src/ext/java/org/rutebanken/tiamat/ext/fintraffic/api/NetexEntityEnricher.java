package org.rutebanken.tiamat.ext.fintraffic.api;

import org.rutebanken.netex.model.EntityInVersionStructure;

public interface NetexEntityEnricher {
    void enrich(EntityInVersionStructure entity);
}

