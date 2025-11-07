/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.time.Instant;

@MappedSuperclass
public class EntityInVersionStructure extends EntityStructure {


    @Transient
    protected String dataSourceRef;
    protected Instant created;
    protected Instant changed;
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
    @AttributeOverrides({
            @AttributeOverride(name = "fromDate", column = @Column(name = "from_date")),
            @AttributeOverride(name = "toDate", column = @Column(name = "to_date"))
    })
    @Embedded
    private ValidBetween validBetween;

    public String getDataSourceRef() {
        return dataSourceRef;
    }


    public void setDataSourceRef(String value) {
        this.dataSourceRef = value;
    }


    public Instant getCreated() {
        return created;
    }


    public void setCreated(Instant value) {
        this.created = value;
    }


    public Instant getChanged() {
        return changed;
    }


    public void setChanged(Instant value) {
        this.changed = value;
    }

    public ValidBetween getValidBetween() {
        return validBetween;
    }

    public void setValidBetween(ValidBetween validBetween) {
        this.validBetween = validBetween;
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

    public void mergeWithExistingVersion(EntityInVersionStructure existingVersion) {
        // By default, do nothing
    }
}
