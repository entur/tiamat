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


public abstract class AccessEquipment_VersionStructure
        extends InstalledEquipment_VersionStructure {

    protected BigDecimal width;
    protected DirectionOfUseEnumeration directionOfUse;
    protected BigInteger passengersPerMinute;
    protected BigInteger relativeWeighting;

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal value) {
        this.width = value;
    }

    public DirectionOfUseEnumeration getDirectionOfUse() {
        return directionOfUse;
    }

    public void setDirectionOfUse(DirectionOfUseEnumeration value) {
        this.directionOfUse = value;
    }

    public BigInteger getPassengersPerMinute() {
        return passengersPerMinute;
    }

    public void setPassengersPerMinute(BigInteger value) {
        this.passengersPerMinute = value;
    }

    public BigInteger getRelativeWeighting() {
        return relativeWeighting;
    }

    public void setRelativeWeighting(BigInteger value) {
        this.relativeWeighting = value;
    }

}
