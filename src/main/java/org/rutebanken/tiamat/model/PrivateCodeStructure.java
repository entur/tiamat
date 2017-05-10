package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class PrivateCodeStructure implements Serializable {

    protected String value;
    protected String type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

}
