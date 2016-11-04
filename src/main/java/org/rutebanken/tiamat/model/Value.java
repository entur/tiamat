package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Value class for use with Map<String, Value> in {@link DataManagedObjectStructure}.
 */
@Entity
public class Value {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ElementCollection(fetch = FetchType.EAGER)
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

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("items", items)
                .toString();
    }
}
