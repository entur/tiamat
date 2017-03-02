package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.indentification.IdentifiedEntity;

public interface IdentifiedEntityRepository<T extends IdentifiedEntity> {

    T findByNetexId(String netexId);
}
