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


public class WheelchairVehicleEquipment_VersionStructure
        extends ActualVehicleEquipment_VersionStructure {

    protected Boolean hasWheelChairSpaces;
    protected BigInteger numberOfWheelchairAreas;
    protected BigDecimal widthOfAccessArea;
    protected BigDecimal lengthOfAccessArea;
    protected BigDecimal heightOfAccessArea;
    protected BigDecimal wheelchairTurningCircle;
    protected Boolean companionSeat;

    public Boolean isHasWheelChairSpaces() {
        return hasWheelChairSpaces;
    }

    public void setHasWheelChairSpaces(Boolean value) {
        this.hasWheelChairSpaces = value;
    }

    public BigInteger getNumberOfWheelchairAreas() {
        return numberOfWheelchairAreas;
    }

    public void setNumberOfWheelchairAreas(BigInteger value) {
        this.numberOfWheelchairAreas = value;
    }

    public BigDecimal getWidthOfAccessArea() {
        return widthOfAccessArea;
    }

    public void setWidthOfAccessArea(BigDecimal value) {
        this.widthOfAccessArea = value;
    }

    public BigDecimal getLengthOfAccessArea() {
        return lengthOfAccessArea;
    }

    public void setLengthOfAccessArea(BigDecimal value) {
        this.lengthOfAccessArea = value;
    }

    public BigDecimal getHeightOfAccessArea() {
        return heightOfAccessArea;
    }

    public void setHeightOfAccessArea(BigDecimal value) {
        this.heightOfAccessArea = value;
    }

    public BigDecimal getWheelchairTurningCircle() {
        return wheelchairTurningCircle;
    }

    public void setWheelchairTurningCircle(BigDecimal value) {
        this.wheelchairTurningCircle = value;
    }

    public Boolean isCompanionSeat() {
        return companionSeat;
    }

    public void setCompanionSeat(Boolean value) {
        this.companionSeat = value;
    }

}
