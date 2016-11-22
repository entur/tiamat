package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
public class Value {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ElementCollection
    @CollectionTable(
            name="items",
            indexes = @Index(name = "items_index", columnList = "items")
    )
    private Set<String> items = new HashSet<>();

    public Value() {
    }

    public Value(String... items) {
        Collections.addAll(this.items, items);
    }

    public Value(List<String> items) {
        this.items.addAll(items);
    }

    public Set<String> getItems() {
        return items;
    }

    public void setItems(Set<String> items) {
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
