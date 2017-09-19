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

import java.math.BigDecimal;
import java.math.BigInteger;


public class LuggageLockerEquipment_VersionStructure
        extends SiteEquipment_VersionStructure {

    protected BigInteger numberOfLockers;
    protected BigDecimal lockerWidth;
    protected BigDecimal lockerHeight;
    protected BigDecimal lockerDepth;
    protected LockerTypeEnumeration lockerType;

    public BigInteger getNumberOfLockers() {
        return numberOfLockers;
    }

    public void setNumberOfLockers(BigInteger value) {
        this.numberOfLockers = value;
    }

    public BigDecimal getLockerWidth() {
        return lockerWidth;
    }

    public void setLockerWidth(BigDecimal value) {
        this.lockerWidth = value;
    }

    public BigDecimal getLockerHeight() {
        return lockerHeight;
    }

    public void setLockerHeight(BigDecimal value) {
        this.lockerHeight = value;
    }

    public BigDecimal getLockerDepth() {
        return lockerDepth;
    }

    public void setLockerDepth(BigDecimal value) {
        this.lockerDepth = value;
    }

    public LockerTypeEnumeration getLockerType() {
        return lockerType;
    }

    public void setLockerType(LockerTypeEnumeration value) {
        this.lockerType = value;
    }

}
