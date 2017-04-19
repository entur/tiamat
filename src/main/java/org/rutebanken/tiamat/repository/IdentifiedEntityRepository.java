package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.QueryHint;
import java.util.List;

public interface IdentifiedEntityRepository<T extends IdentifiedEntity> {

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    T findFirstByNetexIdOrderByVersionDesc(String netexId);

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    T findFirstByNetexIdAndVersion(String netexId, long version);

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    List<T> findByNetexId(String netexId);
}
