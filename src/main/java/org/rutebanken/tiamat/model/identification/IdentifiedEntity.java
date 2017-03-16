package org.rutebanken.tiamat.model.identification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.rutebanken.tiamat.netex.id.NetexIdAssigner;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.IdentifiedEntityListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@MappedSuperclass
@EntityListeners(IdentifiedEntityListener.class)
public abstract class IdentifiedEntity {

    @Id
    @GeneratedValue(generator="sequence_per_table_generator")
    protected Long id;

    protected String netexId;


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
