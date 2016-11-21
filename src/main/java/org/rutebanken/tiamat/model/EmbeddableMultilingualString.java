package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;

@Embeddable
public class EmbeddableMultilingualString extends MultilingualString {
    public EmbeddableMultilingualString() {
    }

    public EmbeddableMultilingualString(String value, String lang) {
        this.value = value;
        this.lang = lang;
    }

    public EmbeddableMultilingualString(String value) {
        this.value = value;
    }

}
