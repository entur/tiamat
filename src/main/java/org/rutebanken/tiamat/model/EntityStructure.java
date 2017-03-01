package org.rutebanken.tiamat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.rutebanken.tiamat.model.indentification.IdentifiedEntity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


@MappedSuperclass
public abstract class EntityStructure implements Serializable, IdentifiedEntity {

    @Id
    @GeneratedValue(generator="idgen")
    @GenericGenerator(name = "idgen",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = SequenceStyleGenerator.CONFIG_PREFER_SEQUENCE_PER_ENTITY, value = "true"),
                    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "10")
            })
    protected Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

}
