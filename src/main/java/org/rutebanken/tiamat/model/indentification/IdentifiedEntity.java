package org.rutebanken.tiamat.model.indentification;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class IdentifiedEntity {

    @Id
    @GeneratedValue(generator="sequence_per_table_generator")
    protected Long id;

    @Column(unique = true)
    protected String netexId;

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
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
