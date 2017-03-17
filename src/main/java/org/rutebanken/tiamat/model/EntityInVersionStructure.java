package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public class EntityInVersionStructure extends EntityStructure {

    @OneToMany(cascade = CascadeType.ALL)
    private final List<AvailabilityCondition> availabilityConditions = new ArrayList<>();

    @Transient
    protected String dataSourceRef;

    protected ZonedDateTime created;

    protected ZonedDateTime changed;

    @Transient
    protected ModificationEnumeration modification;

    protected long version;

    @Transient
    protected StatusEnumeration status;

    @Transient
    protected String derivedFromVersionRef;

    @Transient
    protected String compatibleWithVersionFrameVersionRef;

    @Transient
    protected String derivedFromObjectRef;

    public List<AvailabilityCondition> getAvailabilityConditions() {
        return availabilityConditions;
    }

    public String getDataSourceRef() {
        return dataSourceRef;
    }


    public void setDataSourceRef(String value) {
        this.dataSourceRef = value;
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


    public ModificationEnumeration getModification() {
        if (modification == null) {
            return ModificationEnumeration.NEW;
        } else {
            return modification;
        }
    }


    public void setModification(ModificationEnumeration value) {
        this.modification = value;
    }


    public long getVersion() {
        return version;
    }


    public void setVersion(long value) {
        this.version = value;
    }


    public StatusEnumeration getStatus() {
        if (status == null) {
            return StatusEnumeration.ACTIVE;
        } else {
            return status;
        }
    }


    public void setStatus(StatusEnumeration value) {
        this.status = value;
    }


    public String getDerivedFromVersionRef() {
        return derivedFromVersionRef;
    }


    public void setDerivedFromVersionRef(String value) {
        this.derivedFromVersionRef = value;
    }


    public String getCompatibleWithVersionFrameVersionRef() {
        return compatibleWithVersionFrameVersionRef;
    }


    public void setCompatibleWithVersionFrameVersionRef(String value) {
        this.compatibleWithVersionFrameVersionRef = value;
    }


    public String getDerivedFromObjectRef() {
        return derivedFromObjectRef;
    }


    public void setDerivedFromObjectRef(String value) {
        this.derivedFromObjectRef = value;
    }

}
