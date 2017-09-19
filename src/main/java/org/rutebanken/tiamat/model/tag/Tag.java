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

package org.rutebanken.tiamat.model.tag;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@IdClass(TagPK.class)
public class Tag implements Serializable {

    /**
     * Unversioned reference to netexId.
     * A tag can point to any EntityInVersionStructure
     */
    @Id
    @Column(name = "netex_reference")
    private String idReference;

    @Id
    private String name;

    private String createdBy;

    private Instant created;

    private String comment;

    private Instant removed;

    private String removedBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getRemoved() {
        return removed;
    }

    public void setRemoved(Instant removed) {
        this.removed = removed;
    }

    public String getRemovedBy() {
        return removedBy;
    }

    public void setRemovedBy(String removedBy) {
        this.removedBy = removedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (idReference != null ? !idReference.equals(tag.idReference) : tag.idReference != null) return false;
        if (createdBy != null ? !createdBy.equals(tag.createdBy) : tag.createdBy != null) return false;
        if (name != null ? !name.equals(tag.name) : tag.name != null) return false;
        if (created != null ? !created.equals(tag.created) : tag.created != null) return false;
        if (comment != null ? !comment.equals(tag.comment) : tag.comment != null) return false;
        if (removed != null ? !removed.equals(tag.removed) : tag.removed != null) return false;
        if (removedBy != null ? !removedBy.equals(tag.removedBy) : tag.removedBy != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = createdBy != null ? createdBy.hashCode() : 0;
        result = 31 * result + (idReference != null ? idReference.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (removed != null ? removed.hashCode() : 0);
        result = 31 * result + (removedBy != null ? removedBy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("idReference", idReference)
                .add("name", name)
                .add("createdBy", createdBy)
                .add("created", created)
                .add("comment", comment)
                .add("removed", removed)
                .add("removedBy", removedBy)
                .toString();
    }

    public String getIdReference() {
        return idReference;
    }

    public void setIdreference(String netexReference) {
        this.idReference = netexReference;
    }
}
