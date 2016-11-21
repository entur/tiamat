package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;


@Entity
public class Value {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ElementCollection
    private Set<String> items = new HashSet<>();

    public Value() {}

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
