package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public class LiftEquipment_VersionStructure
        extends AccessEquipment_VersionStructure {

    protected BigDecimal depth;
    protected BigDecimal maximumLoad;
    protected Boolean wheelchairPasssable;
    protected BigDecimal wheelchairTurningCircle;
    protected BigDecimal internalWidth;
    protected HandrailEnumeration handrailType;
    protected BigDecimal handrailHeight;
    protected BigDecimal lowerHandrailHeight;
    protected BigDecimal callButtonHeight;
    protected BigDecimal directionButtonHeight;
    protected Boolean raisedButtons;
    protected Boolean brailleButtons;
    protected Boolean throughLoader;
    protected Boolean mirrorOnOppositeSide;
    protected Boolean attendant;
    protected Boolean automatic;
    protected Boolean alarmButton;
    protected Boolean tactileActuators;
    protected Boolean accousticAnnouncements;
    protected Boolean signageToLift;
    protected Boolean suitableForCycles;

    public BigDecimal getDepth() {
        return depth;
    }

    public void setDepth(BigDecimal value) {
        this.depth = value;
    }

    public BigDecimal getMaximumLoad() {
        return maximumLoad;
    }

    public void setMaximumLoad(BigDecimal value) {
        this.maximumLoad = value;
    }

    public Boolean isWheelchairPasssable() {
        return wheelchairPasssable;
    }

    public void setWheelchairPasssable(Boolean value) {
        this.wheelchairPasssable = value;
    }

    public BigDecimal getWheelchairTurningCircle() {
        return wheelchairTurningCircle;
    }

    public void setWheelchairTurningCircle(BigDecimal value) {
        this.wheelchairTurningCircle = value;
    }

    public BigDecimal getInternalWidth() {
        return internalWidth;
    }

    public void setInternalWidth(BigDecimal value) {
        this.internalWidth = value;
    }

    public HandrailEnumeration getHandrailType() {
        return handrailType;
    }

    public void setHandrailType(HandrailEnumeration value) {
        this.handrailType = value;
    }

    public BigDecimal getHandrailHeight() {
        return handrailHeight;
    }

    public void setHandrailHeight(BigDecimal value) {
        this.handrailHeight = value;
    }

    public BigDecimal getLowerHandrailHeight() {
        return lowerHandrailHeight;
    }

    public void setLowerHandrailHeight(BigDecimal value) {
        this.lowerHandrailHeight = value;
    }

    public BigDecimal getCallButtonHeight() {
        return callButtonHeight;
    }

    public void setCallButtonHeight(BigDecimal value) {
        this.callButtonHeight = value;
    }

    public BigDecimal getDirectionButtonHeight() {
        return directionButtonHeight;
    }

    public void setDirectionButtonHeight(BigDecimal value) {
        this.directionButtonHeight = value;
    }

    public Boolean isRaisedButtons() {
        return raisedButtons;
    }

    public void setRaisedButtons(Boolean value) {
        this.raisedButtons = value;
    }

    public Boolean isBrailleButtons() {
        return brailleButtons;
    }

    public void setBrailleButtons(Boolean value) {
        this.brailleButtons = value;
    }

    public Boolean isThroughLoader() {
        return throughLoader;
    }

    public void setThroughLoader(Boolean value) {
        this.throughLoader = value;
    }

    public Boolean isMirrorOnOppositeSide() {
        return mirrorOnOppositeSide;
    }

    public void setMirrorOnOppositeSide(Boolean value) {
        this.mirrorOnOppositeSide = value;
    }

    public Boolean isAttendant() {
        return attendant;
    }

    public void setAttendant(Boolean value) {
        this.attendant = value;
    }

    public Boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(Boolean value) {
        this.automatic = value;
    }

    public Boolean isAlarmButton() {
        return alarmButton;
    }

    public void setAlarmButton(Boolean value) {
        this.alarmButton = value;
    }

    public Boolean isTactileActuators() {
        return tactileActuators;
    }

    public void setTactileActuators(Boolean value) {
        this.tactileActuators = value;
    }

    public Boolean isAccousticAnnouncements() {
        return accousticAnnouncements;
    }

    public void setAccousticAnnouncements(Boolean value) {
        this.accousticAnnouncements = value;
    }

    public Boolean isSignageToLift() {
        return signageToLift;
    }

    public void setSignageToLift(Boolean value) {
        this.signageToLift = value;
    }

    public Boolean isSuitableForCycles() {
        return suitableForCycles;
    }

    public void setSuitableForCycles(Boolean value) {
        this.suitableForCycles = value;
    }

}
