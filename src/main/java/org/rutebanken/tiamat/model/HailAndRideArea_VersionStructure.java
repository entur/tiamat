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


public class HailAndRideArea_VersionStructure
        extends FlexibleQuay_VersionStructure {

    protected String bearingCompass;
    protected BigInteger bearingDegrees;
    protected DestinationDisplayViews_RelStructure destinations;
    protected PointRefStructure startPointRef;
    protected PointRefStructure endPointRef;

    public String getBearingCompass() {
        return bearingCompass;
    }

    public void setBearingCompass(String value) {
        this.bearingCompass = value;
    }

    public BigInteger getBearingDegrees() {
        return bearingDegrees;
    }

    public void setBearingDegrees(BigInteger value) {
        this.bearingDegrees = value;
    }

    public DestinationDisplayViews_RelStructure getDestinations() {
        return destinations;
    }

    public void setDestinations(DestinationDisplayViews_RelStructure value) {
        this.destinations = value;
    }

    public PointRefStructure getStartPointRef() {
        return startPointRef;
    }

    public void setStartPointRef(PointRefStructure value) {
        this.startPointRef = value;
    }

    public PointRefStructure getEndPointRef() {
        return endPointRef;
    }

    public void setEndPointRef(PointRefStructure value) {
        this.endPointRef = value;
    }

}
