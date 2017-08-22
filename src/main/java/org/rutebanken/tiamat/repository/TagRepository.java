package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Component;

import javax.persistence.QueryHint;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Set<Tag> findByNetexReference(String ref);

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Set<Tag> findByType(String type);

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Set<Tag> findByTypeAndNetexReference(String type, String netexReference);
}
