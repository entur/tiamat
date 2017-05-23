package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PrivateCodeStructure implements Serializable {

    protected String value;
    protected String type;

    public PrivateCodeStructure() {
    }

    public PrivateCodeStructure(String value, String type) {
        this.value = value;
        this.type = type;
    }

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

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof PrivateCodeStructure)) {
            return false;
        }

        PrivateCodeStructure other = (PrivateCodeStructure) object;

        return Objects.equals(this.value, other.value)
                && Objects.equals(this.type, other.type);
    }
}
