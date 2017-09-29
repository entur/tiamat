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

import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.math.BigInteger;

@MappedSuperclass
public abstract class WaitingEquipment_VersionStructure
        extends SiteEquipment_VersionStructure {

    protected BigInteger seats;
    protected BigDecimal width;
    protected BigDecimal length;
    protected Boolean stepFree;
    protected BigDecimal wheelchairAreaWidth;
    protected BigDecimal wheelchairAreaLength;
    protected Boolean smokingAllowed;
    protected Boolean heated;
    protected Boolean airConditioned;

    public BigInteger getSeats() {
        return seats;
    }

    public void setSeats(BigInteger value) {
        this.seats = value;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal value) {
        this.width = value;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal value) {
        this.length = value;
    }

    public Boolean isStepFree() {
        return stepFree;
    }

    public void setStepFree(Boolean value) {
        this.stepFree = value;
    }

    public BigDecimal getWheelchairAreaWidth() {
        return wheelchairAreaWidth;
    }

    public void setWheelchairAreaWidth(BigDecimal value) {
        this.wheelchairAreaWidth = value;
    }

    public BigDecimal getWheelchairAreaLength() {
        return wheelchairAreaLength;
    }

    public void setWheelchairAreaLength(BigDecimal value) {
        this.wheelchairAreaLength = value;
    }

    public Boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public void setSmokingAllowed(Boolean value) {
        this.smokingAllowed = value;
    }

    public Boolean isHeated() {
        return heated;
    }

    public void setHeated(Boolean value) {
        this.heated = value;
    }

    public Boolean isAirConditioned() {
        return airConditioned;
    }

    public void setAirConditioned(Boolean value) {
        this.airConditioned = value;
    }

}
