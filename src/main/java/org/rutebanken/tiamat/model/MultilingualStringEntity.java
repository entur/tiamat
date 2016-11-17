package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "multilingual_string", indexes = {@Index(name = "multilingual_value", columnList = "value")})
public class MultilingualStringEntity extends MultilingualString {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    public MultilingualStringEntity() {}

    public MultilingualStringEntity(String value, String lang) {
        this.value = value;
        this.lang = lang;
    }

    public MultilingualStringEntity(String value) {
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
