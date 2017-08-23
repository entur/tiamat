package org.rutebanken.tiamat.model.tag;

import java.io.Serializable;

public class TagPK implements Serializable {

    private String idReference;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagPK tagPK = (TagPK) o;

        if (idReference != null ? !idReference.equals(tagPK.idReference) : tagPK.idReference != null) return false;
        if (name != null ? !name.equals(tagPK.name) : tagPK.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idReference != null ? idReference.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
