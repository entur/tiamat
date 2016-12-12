package org.rutebanken.tiamat.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Accesses_RelStructure
        extends ContainmentAggregationStructure {

    @Column
    @ElementCollection(targetClass = AccessRefStructure.class)
    protected List<AccessRefStructure> accessRefOrAccess;

    public List<AccessRefStructure> getAccessRefOrAccess() {
        if (accessRefOrAccess == null) {
            accessRefOrAccess = new ArrayList<AccessRefStructure>();
        }
        return this.accessRefOrAccess;
    }

}
