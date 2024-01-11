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
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface EntityInVersionRepository<T extends EntityInVersionStructure> extends JpaRepository<T, Long> {

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    T findFirstByNetexIdOrderByVersionDesc(String netexId);

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    T findFirstByNetexIdAndVersion(String netexId, long version);

    @QueryHints(value = { @QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
    List<T> findByNetexId(String netexId);
}
