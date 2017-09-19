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


public class RoadNumberRangeStructure {

    protected BigInteger fromNumber;
    protected BigInteger toNumber;

    public BigInteger getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(BigInteger value) {
        this.fromNumber = value;
    }

    public BigInteger getToNumber() {
        return toNumber;
    }

    public void setToNumber(BigInteger value) {
        this.toNumber = value;
    }

}
