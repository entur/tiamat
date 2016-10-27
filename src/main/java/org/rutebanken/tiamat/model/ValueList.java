package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * List of values wrapper class
 */
@Entity
public class ValueList {

    @GeneratedValue
    @Id
    private long id;

    @ElementCollection
    private List<String> values = new ArrayList<>(0);

    public ValueList() {}

    public ValueList(String... values) {
        Collections.addAll(this.values, values);
    }

    public ValueList(List<String> values) {
        this.values.addAll(values);
    }

    public List<String> getValues() {
        return values;
    }
}
