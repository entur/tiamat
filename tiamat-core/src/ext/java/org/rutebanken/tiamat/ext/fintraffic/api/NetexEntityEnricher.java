package org.rutebanken.tiamat.ext.fintraffic.api;

import org.entur.netex2.model.EntityInVersionStructure;

public interface NetexEntityEnricher {
    void enrich(EntityInVersionStructure entity);
}

