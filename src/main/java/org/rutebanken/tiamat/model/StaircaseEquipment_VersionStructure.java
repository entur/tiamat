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


public class StaircaseEquipment_VersionStructure
        extends StairEquipment_VersionStructure {

    protected Boolean continuousHandrail;
    protected Boolean spiralStair;
    protected BigInteger numberOfFlights;
    protected StairFlights_RelStructure flights;

    public Boolean isContinuousHandrail() {
        return continuousHandrail;
    }

    public void setContinuousHandrail(Boolean value) {
        this.continuousHandrail = value;
    }

    public Boolean isSpiralStair() {
        return spiralStair;
    }

    public void setSpiralStair(Boolean value) {
        this.spiralStair = value;
    }

    public BigInteger getNumberOfFlights() {
        return numberOfFlights;
    }

    public void setNumberOfFlights(BigInteger value) {
        this.numberOfFlights = value;
    }

    public StairFlights_RelStructure getFlights() {
        return flights;
    }

    public void setFlights(StairFlights_RelStructure value) {
        this.flights = value;
    }

}
