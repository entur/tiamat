package org.rutebanken.tiamat.repository;

import org.rutebanken.tiamat.model.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Component;

import javax.persistence.QueryHint;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Set<Tag> findByIdReference(String ref);

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Set<Tag> findByName(String name);

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Tag findByNameAndIdReference(String name, String idReference);
}
