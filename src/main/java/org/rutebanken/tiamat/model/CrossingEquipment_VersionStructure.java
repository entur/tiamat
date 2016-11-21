

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class CrossingEquipment_VersionStructure
    extends AccessEquipment_VersionStructure
{

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
