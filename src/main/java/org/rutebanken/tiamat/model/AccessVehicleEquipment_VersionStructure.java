

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "lowFloor",
    "highFloor",
    "hoist",
    "hoistOperatingRadius",
    "ramp",
    "rampBearingCapacity",
    "numberOfSteps",
    "boardingHeight",
    "gapToPlatform",
    "widthOfAccessArea",
    "heightOfAccessArea",
    "automaticDoors",
    "suitableFor",
    "assistanceNeeded",
    "assistedBoardingLocation",
public class AccessVehicleEquipment_VersionStructure
    extends ActualVehicleEquipment_VersionStructure
{

    protected Boolean lowFloor;
    protected Boolean highFloor;
    protected Boolean hoist;
    protected BigDecimal hoistOperatingRadius;
    protected Boolean ramp;
    protected BigDecimal rampBearingCapacity;
    protected BigInteger numberOfSteps;
    protected TypeOfEntity_VersionStructure boardingHeight;
    protected TypeOfEntity_VersionStructure gapToPlatform;
    protected BigDecimal widthOfAccessArea;
    protected BigDecimal heightOfAccessArea;
    protected Boolean automaticDoors;
    protected List<MobilityEnumeration> suitableFor;
    protected AssistanceNeededEnumeration assistanceNeeded;
    protected AssistedBoardingLocationEnumeration assistedBoardingLocation;
    protected Boolean guideDogsAllowed;

    public Boolean isLowFloor() {
        return lowFloor;
    }

    public void setLowFloor(Boolean value) {
        this.lowFloor = value;
    }

    public Boolean isHighFloor() {
        return highFloor;
    }

    public void setHighFloor(Boolean value) {
        this.highFloor = value;
    }

    public Boolean isHoist() {
        return hoist;
    }

    public void setHoist(Boolean value) {
        this.hoist = value;
    }

    public BigDecimal getHoistOperatingRadius() {
        return hoistOperatingRadius;
    }

    public void setHoistOperatingRadius(BigDecimal value) {
        this.hoistOperatingRadius = value;
    }

    public Boolean isRamp() {
        return ramp;
    }

    public void setRamp(Boolean value) {
        this.ramp = value;
    }

    public BigDecimal getRampBearingCapacity() {
        return rampBearingCapacity;
    }

    public void setRampBearingCapacity(BigDecimal value) {
        this.rampBearingCapacity = value;
    }

    public BigInteger getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(BigInteger value) {
        this.numberOfSteps = value;
    }

    public TypeOfEntity_VersionStructure getBoardingHeight() {
        return boardingHeight;
    }

    public void setBoardingHeight(TypeOfEntity_VersionStructure value) {
        this.boardingHeight = value;
    }

    public TypeOfEntity_VersionStructure getGapToPlatform() {
        return gapToPlatform;
    }

    public void setGapToPlatform(TypeOfEntity_VersionStructure value) {
        this.gapToPlatform = value;
    }

    public BigDecimal getWidthOfAccessArea() {
        return widthOfAccessArea;
    }

    public void setWidthOfAccessArea(BigDecimal value) {
        this.widthOfAccessArea = value;
    }

    public BigDecimal getHeightOfAccessArea() {
        return heightOfAccessArea;
    }

    public void setHeightOfAccessArea(BigDecimal value) {
        this.heightOfAccessArea = value;
    }

    public Boolean isAutomaticDoors() {
        return automaticDoors;
    }

    public void setAutomaticDoors(Boolean value) {
        this.automaticDoors = value;
    }

    public List<MobilityEnumeration> getSuitableFor() {
        if (suitableFor == null) {
            suitableFor = new ArrayList<MobilityEnumeration>();
        }
        return this.suitableFor;
    }

    public AssistanceNeededEnumeration getAssistanceNeeded() {
        return assistanceNeeded;
    }

    public void setAssistanceNeeded(AssistanceNeededEnumeration value) {
        this.assistanceNeeded = value;
    }

    public AssistedBoardingLocationEnumeration getAssistedBoardingLocation() {
        return assistedBoardingLocation;
    }

    public void setAssistedBoardingLocation(AssistedBoardingLocationEnumeration value) {
        this.assistedBoardingLocation = value;
    }

    public Boolean isGuideDogsAllowed() {
        return guideDogsAllowed;
    }

    public void setGuideDogsAllowed(Boolean value) {
        this.guideDogsAllowed = value;
    }

}
