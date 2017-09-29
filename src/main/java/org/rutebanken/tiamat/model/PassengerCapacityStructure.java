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

import java.math.BigInteger;


public class PassengerCapacityStructure
        extends DataManagedObjectStructure {
    protected BigInteger totalCapacity;
    protected BigInteger seatingCapacity;
    protected BigInteger standingCapacity;
    protected BigInteger specialPlaceCapacity;
    protected BigInteger pushchairCapacity;
    protected BigInteger wheelchairPlaceCapacity;

    public BigInteger getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(BigInteger value) {
        this.totalCapacity = value;
    }

    public BigInteger getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(BigInteger value) {
        this.seatingCapacity = value;
    }

    public BigInteger getStandingCapacity() {
        return standingCapacity;
    }

    public void setStandingCapacity(BigInteger value) {
        this.standingCapacity = value;
    }

    public BigInteger getSpecialPlaceCapacity() {
        return specialPlaceCapacity;
    }

    public void setSpecialPlaceCapacity(BigInteger value) {
        this.specialPlaceCapacity = value;
    }

    public BigInteger getPushchairCapacity() {
        return pushchairCapacity;
    }

    public void setPushchairCapacity(BigInteger value) {
        this.pushchairCapacity = value;
    }

    public BigInteger getWheelchairPlaceCapacity() {
        return wheelchairPlaceCapacity;
    }

    public void setWheelchairPlaceCapacity(BigInteger value) {
        this.wheelchairPlaceCapacity = value;
    }

}
