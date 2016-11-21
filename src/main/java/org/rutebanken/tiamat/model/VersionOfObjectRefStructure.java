package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
public class VersionOfObjectRefStructure implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    protected String value;

    protected String ref;

    protected ZonedDateTime created;

    protected ZonedDateTime changed;

    protected String version;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String value) {
        this.ref = value;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime value) {
        this.created = value;
    }

    public ZonedDateTime getChanged() {
        return changed;
    }

    public void setChanged(ZonedDateTime value) {
        this.changed = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        this.version = value;
    }
}
