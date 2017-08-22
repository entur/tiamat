package org.rutebanken.tiamat.model.tag;

import com.google.common.base.MoreObjects;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "tag_netex_reference_type_constraint", columnNames = {"netex_reference", "type"})})
public class Tag {

    @Id
    @GeneratedValue
    private long id;

    /**
     * Unversioned site ref.
     * A tag can point to any entity with Netex ID.
     */
    @Column(name = "netex_reference")
    private String netexReference;
    
    private String createdBy;

    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

        if (netexReference != null ? !netexReference.equals(tag.netexReference) : tag.netexReference != null) return false;
        if (createdBy != null ? !createdBy.equals(tag.createdBy) : tag.createdBy != null) return false;
        if (type != null ? !type.equals(tag.type) : tag.type != null) return false;
        if (created != null ? !created.equals(tag.created) : tag.created != null) return false;
        if (comment != null ? !comment.equals(tag.comment) : tag.comment != null) return false;
        if (removed != null ? !removed.equals(tag.removed) : tag.removed != null) return false;
        if (removedBy != null ? !removedBy.equals(tag.removedBy) : tag.removedBy != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = createdBy != null ? createdBy.hashCode() : 0;
        result = 31 * result + (netexReference != null ? netexReference.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (removed != null ? removed.hashCode() : 0);
        result = 31 * result + (removedBy != null ? removedBy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("netexReference", netexReference)
                .add("createdBy", createdBy)
                .add("type", type)
                .add("created", created)
                .add("comment", comment)
                .add("removed", removed)
                .add("removedBy", removedBy)
                .toString();
    }

    public String getNetexReference() {
        return netexReference;
    }

    public void setNetexReference(String netexReference) {
        this.netexReference = netexReference;
    }
}
