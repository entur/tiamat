

package org.rutebanken.tiamat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;

import static org.hibernate.id.SequenceGenerator.SEQUENCE;


@MappedSuperclass
public abstract class EntityStructure implements Serializable{


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

}
