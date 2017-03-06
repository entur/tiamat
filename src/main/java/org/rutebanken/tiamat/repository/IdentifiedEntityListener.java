package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.config.ApplicationContextProvider;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import javax.persistence.PrePersist;

public class IdentifiedEntityListener {


    @PrePersist
    public void assignNetexId(IdentifiedEntity identifiedEntity) {
        // This class is not managed by Spring
        ApplicationContextProvider.getNetexIdAssigner().assignNetexId(identifiedEntity);
    }
}
