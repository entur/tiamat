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

package org.rutebanken.tiamat.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@MappedSuperclass
public abstract class DataManagedObjectStructure
        extends EntityInVersionStructure {

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final Map<String, Value> keyValues = new HashMap<>();

    @Transient
    protected ExtensionsStructure extensions;

    @Transient
    protected String responsibilitySetRef;

    private String versionComment;

    private String changedBy;

    public ExtensionsStructure getExtensions() {
        return extensions;
    }

    public void setExtensions(ExtensionsStructure value) {
        this.extensions = value;
    }

    public String getResponsibilitySetRef() {
        return responsibilitySetRef;
    }

    public void setResponsibilitySetRef(String value) {
        this.responsibilitySetRef = value;
    }

    public Map<String, Value> getKeyValues() {
        return keyValues;
    }

    public Set<String> getOrCreateValues(String key) {
        if (keyValues.get(key) == null) {
            keyValues.put(key, new Value());
        }

        return keyValues.get(key).getItems();
    }

    /**
     * Returns the items for the given key, or an empty set if the key is absent.
     * Unlike {@link #getOrCreateValues(String)}, this method never modifies
     * the {@code keyValues} map — safe to call on Hibernate-managed entities
     * and from {@code hashCode()}/{@code equals()}.
     */
    public Set<String> getValues(String key) {
        Value value = keyValues.get(key);
        return value != null ? value.getItems() : Set.of();
    }

    public Set<String> getOriginalIds() {
        return getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY);
    }

    public String getVersionComment() {
        return versionComment;
    }

    public void setVersionComment(String versionComment) {
        this.versionComment = versionComment;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}
