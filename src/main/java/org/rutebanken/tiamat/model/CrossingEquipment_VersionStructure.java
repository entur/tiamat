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

public class CrossingEquipment_VersionStructure
        extends AccessEquipment_VersionStructure {

    protected CrossingTypeEnumeration crossingType;
    protected Boolean zebraCrossing;
    protected Boolean pedestrianLights;
    protected Boolean acousticDeviceSensors;
    protected Boolean acousticCrossingAids;
    protected Boolean tactileGuidanceStrips;
    protected Boolean visualGuidanceBands;
    protected Boolean droppedKerb;
    protected Boolean suitableForCycles;

    public CrossingTypeEnumeration getCrossingType() {
        return crossingType;
    }

    public void setCrossingType(CrossingTypeEnumeration value) {
        this.crossingType = value;
    }

    public Boolean isZebraCrossing() {
        return zebraCrossing;
    }

    public void setZebraCrossing(Boolean value) {
        this.zebraCrossing = value;
    }

    public Boolean isPedestrianLights() {
        return pedestrianLights;
    }

    public void setPedestrianLights(Boolean value) {
        this.pedestrianLights = value;
    }

    public Boolean isAcousticDeviceSensors() {
        return acousticDeviceSensors;
    }

    public void setAcousticDeviceSensors(Boolean value) {
        this.acousticDeviceSensors = value;
    }

    public Boolean isAcousticCrossingAids() {
        return acousticCrossingAids;
    }

    public void setAcousticCrossingAids(Boolean value) {
        this.acousticCrossingAids = value;
    }

    public Boolean isTactileGuidanceStrips() {
        return tactileGuidanceStrips;
    }

    public void setTactileGuidanceStrips(Boolean value) {
        this.tactileGuidanceStrips = value;
    }

    public Boolean isVisualGuidanceBands() {
        return visualGuidanceBands;
    }

    public void setVisualGuidanceBands(Boolean value) {
        this.visualGuidanceBands = value;
    }

    public Boolean isDroppedKerb() {
        return droppedKerb;
    }

    public void setDroppedKerb(Boolean value) {
        this.droppedKerb = value;
    }

    public Boolean isSuitableForCycles() {
        return suitableForCycles;
    }

    public void setSuitableForCycles(Boolean value) {
        this.suitableForCycles = value;
    }

}
