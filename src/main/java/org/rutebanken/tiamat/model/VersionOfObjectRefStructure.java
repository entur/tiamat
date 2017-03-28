package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class VersionOfObjectRefStructure implements Serializable {

    public static final String ANY_VERSION = "any";

    private String ref;

    private String version;

    public VersionOfObjectRefStructure() {
    }

    public VersionOfObjectRefStructure(String ref, String version) {
        this.ref = ref;
        this.version = version;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String value) {
        this.ref = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        this.version = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ref", ref)
                .add("version", version)
                .toString();
    }
}
