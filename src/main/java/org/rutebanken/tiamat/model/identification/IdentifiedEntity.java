package org.rutebanken.tiamat.model.identification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import javax.persistence.*;

@MappedSuperclass
@Table(indexes = {@Index(name = "netex_id_index", columnList = "netex_id")})
public abstract class IdentifiedEntity {

    @Id
    @GeneratedValue(generator="sequence_per_table_generator")
    protected Long id;

    @Column(unique = true)
    protected String netexId;


    @PrePersist
    public void assignNetexId() {
        if(this.netexId == null) {
            this.netexId = NetexIdMapper.generateNetexId(this);
        }
    }

    private Long getId() {
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
