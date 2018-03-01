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

package org.rutebanken.tiamat.model.identification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.rutebanken.tiamat.repository.listener.IdentifiedEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(IdentifiedEntityListener.class)
public abstract class IdentifiedEntity {

    @Id
    @GeneratedValue(generator="sequence_per_table_generator")
    protected Long id;

    protected String netexId;

    /**
     * This is the primary identificator. Usually, you should relate to ${getNetexId}
     * @return the primary long value of this identitifed entity.
     */
    public Long getId() {
        return id;
    }

    @JsonIgnore
    private void setId(Long id) {
        this.id = id;
    }

    /**
     * Public ID.
     * Typically a NeTEx ID like NSR:StopPlace:123
     * @return the public ID
     */
    public String getNetexId() {
        return netexId;
    }

    public void setNetexId(String netexId) {
        this.netexId = netexId;
    }
}
