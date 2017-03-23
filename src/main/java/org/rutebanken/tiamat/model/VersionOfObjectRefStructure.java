package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class VersionOfObjectRefStructure implements Serializable {

    public static final String ANY_VERSION = "any";

    private String ref;

    private String version;

    private String nameOfRefClass;

    public VersionOfObjectRefStructure() {
    }

    public VersionOfObjectRefStructure(String ref, String version, String nameOfRefClass) {
        this.ref = ref;
        this.version = version;
        this.nameOfRefClass = nameOfRefClass;
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

    /**
     * Name of Class of the referenced entity. Allows reflection. Fixed for each entity type.
     */
    public String getNameOfRefClass() {
        return nameOfRefClass;
    }

    public void setNameOfRefClass(String nameOfRefClass) {
        this.nameOfRefClass = nameOfRefClass;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ref", ref)
                .add("version", version)
                .toString();
    }
}
