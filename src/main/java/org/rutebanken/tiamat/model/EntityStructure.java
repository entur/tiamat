package org.rutebanken.tiamat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;


@MappedSuperclass
@GraphQLType
public abstract class EntityStructure implements Serializable {

    @Id
    @GeneratedValue(generator="idgen")
    @GenericGenerator(name = "idgen",
            strategy = "org.rutebanken.tiamat.repository.OptionalIdGenerator",
            parameters = {
                    @Parameter(name = SequenceStyleGenerator.CONFIG_PREFER_SEQUENCE_PER_ENTITY, value = "true")
            })
    @GraphQLField
    protected Long id;

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

}
