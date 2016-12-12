package org.rutebanken.tiamat.model;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "seq_quay_rel_structure")
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
