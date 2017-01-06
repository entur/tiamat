package org.rutebanken.tiamat.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public class RelationshipStructure {

    @Id
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}
