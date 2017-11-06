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

import javax.persistence.Transient;
import java.math.BigInteger;


public class RoadAddress_VersionStructure
        extends Address_VersionStructure {

    protected String gisFeatureRef;

    protected String roadNumber;

    protected MultilingualStringEntity roadName;

    protected String bearingCompass;

    protected BigInteger bearingDegrees;

    @Transient
    protected RoadNumberRangeStructure oddNumberRange;

    @Transient
    protected RoadNumberRangeStructure evenNumberRange;

    public String getGisFeatureRef() {
        return gisFeatureRef;
    }

    public void setGisFeatureRef(String value) {
        this.gisFeatureRef = value;
    }

    public String getRoadNumber() {
        return roadNumber;
    }

    public void setRoadNumber(String value) {
        this.roadNumber = value;
    }

    public MultilingualStringEntity getRoadName() {
        return roadName;
    }

    public void setRoadName(MultilingualStringEntity value) {
        this.roadName = value;
    }

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

    public RoadNumberRangeStructure getOddNumberRange() {
        return oddNumberRange;
    }

    public void setOddNumberRange(RoadNumberRangeStructure value) {
        this.oddNumberRange = value;
    }

    public RoadNumberRangeStructure getEvenNumberRange() {
        return evenNumberRange;
    }

    public void setEvenNumberRange(RoadNumberRangeStructure value) {
        this.evenNumberRange = value;
    }

}
