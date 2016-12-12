package org.rutebanken.tiamat.model;

import javax.persistence.*;

@Entity
public class MultilingualStringEntity extends MultilingualString {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgen")
    @SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "seq_multilingual_string_entity")
    private long id;

    public MultilingualStringEntity() {
    }

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
