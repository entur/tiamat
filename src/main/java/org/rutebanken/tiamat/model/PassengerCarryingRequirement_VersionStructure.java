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

public class PassengerCarryingRequirement_VersionStructure
        extends VehicleRequirement_VersionStructure {

    protected PassengerCapacity passengerCapacity;
    protected Boolean lowFloor;
    protected Boolean hasLiftOrRamp;
    protected Boolean hasHoist;

    public PassengerCapacity getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(PassengerCapacity value) {
        this.passengerCapacity = value;
    }

    public Boolean isLowFloor() {
        return lowFloor;
    }

    public void setLowFloor(Boolean value) {
        this.lowFloor = value;
    }

    public Boolean isHasLiftOrRamp() {
        return hasLiftOrRamp;
    }

    public void setHasLiftOrRamp(Boolean value) {
        this.hasLiftOrRamp = value;
    }

    public Boolean isHasHoist() {
        return hasHoist;
    }

    public void setHasHoist(Boolean value) {
        this.hasHoist = value;
    }

}
