package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Value {

    @GeneratedValue
    @Id
    private long id;

    @ElementCollection
    private List<String> items = new ArrayList<>(0);

    public Value() {}

    public Value(String... items) {
        Collections.addAll(this.items, items);
    }

    public Value(List<String> items) {
        this.items.addAll(items);
    }

    public List<String> getItems() {
        return items;
    }
}
