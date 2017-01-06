package org.rutebanken.tiamat.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Quays_RelStructure
        extends ContainmentAggregationStructure {

    @Column
    @ElementCollection(targetClass = Quay.class)
    protected List<Quay> quayRefOrQuay;

    public List<Quay> getQuayRefOrQuay() {
        if (quayRefOrQuay == null) {
            quayRefOrQuay = new ArrayList<>();
        }
        return this.quayRefOrQuay;
    }

}
