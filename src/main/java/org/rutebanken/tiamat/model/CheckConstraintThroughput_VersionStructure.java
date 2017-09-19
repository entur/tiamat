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

import javax.xml.datatype.Duration;
import java.math.BigInteger;


public class CheckConstraintThroughput_VersionStructure
        extends Assignment_VersionStructure {

    protected CheckConstraintRefStructure checkConstraintRef;
    protected Duration period;
    protected BigInteger maximumPassengers;
    protected BigInteger averagePassengers;
    protected BigInteger wheelchairPassengers;

    public CheckConstraintRefStructure getCheckConstraintRef() {
        return checkConstraintRef;
    }

    public void setCheckConstraintRef(CheckConstraintRefStructure value) {
        this.checkConstraintRef = value;
    }

    public Duration getPeriod() {
        return period;
    }

    public void setPeriod(Duration value) {
        this.period = value;
    }

    public BigInteger getMaximumPassengers() {
        return maximumPassengers;
    }

    public void setMaximumPassengers(BigInteger value) {
        this.maximumPassengers = value;
    }

    public BigInteger getAveragePassengers() {
        return averagePassengers;
    }

    public void setAveragePassengers(BigInteger value) {
        this.averagePassengers = value;
    }

    public BigInteger getWheelchairPassengers() {
        return wheelchairPassengers;
    }

    public void setWheelchairPassengers(BigInteger value) {
        this.wheelchairPassengers = value;
    }

}
