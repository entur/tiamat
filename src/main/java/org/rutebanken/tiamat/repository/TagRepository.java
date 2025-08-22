/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.repository;

import jakarta.persistence.QueryHint;
import org.rutebanken.tiamat.model.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Set<Tag> findByIdReference(String ref);

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Set<Tag> findByNameContaining(String name);

    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    Tag findByNameAndIdReference(String name, String idReference);

    /**
     * Batch loading method for DataLoader - efficiently loads tags by multiple ID references
     * @param idReferences Set of netex ID references to load tags for
     * @return List of tags for the given references (excludes removed tags)
     */
    @Query("SELECT t FROM Tag t WHERE t.idReference IN :idReferences AND t.removed IS NULL")
    @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    List<Tag> findByIdReferencesIn(@Param("idReferences") Set<String> idReferences);
}
