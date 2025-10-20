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

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.rutebanken.netex.model.CoveredEnumeration;

import java.math.BigInteger;

@MappedSuperclass
public class CycleStorageEquipment_VersionStructure
        extends PlaceEquipment_VersionStructure {

    protected BigInteger numberOfSpaces;
    protected CycleStorageEnumeration cycleStorageType;
    @Transient
    protected Boolean cage;
    @Transient
    protected CoveredEnumeration covered;

    public BigInteger getNumberOfSpaces() {
        return numberOfSpaces;
    }

    public void setNumberOfSpaces(BigInteger value) {
        this.numberOfSpaces = value;
    }

    public CycleStorageEnumeration getCycleStorageType() {
        return cycleStorageType;
    }

    public void setCycleStorageType(CycleStorageEnumeration value) {
        this.cycleStorageType = value;
    }

    public Boolean isCage() {
        return cage;
    }

    public void setCage(Boolean value) {
        this.cage = value;
    }

    public CoveredEnumeration isCovered() {
        return covered;
    }

    public void setCovered(CoveredEnumeration value) {
        this.covered = value;
    }

}
