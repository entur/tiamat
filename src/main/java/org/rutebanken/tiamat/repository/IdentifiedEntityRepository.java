package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.util.List;

public interface IdentifiedEntityRepository<T extends IdentifiedEntity> {

    T findFirstByNetexIdOrderByVersionDesc(String netexId);

    T findFirstByNetexIdAndVersion(String netexId, long version);

    List<T> findByNetexId(String netexId);
}
