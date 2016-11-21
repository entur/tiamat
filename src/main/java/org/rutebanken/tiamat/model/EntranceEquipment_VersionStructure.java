

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "door",
    "keptOpen",
    "revolvingDoor",
    "barrier",
    "numberOfGates",
    "staffing",
    "entranceRequiresStaffing",
    "entranceRequiresTicket",
    "entranceRequiresPassport",
    "acousticSensor",
    "automaticDoor",
    "glassDoor",
    "wheelchairPassable",
    "wheechairUnaided",
    "entranceAttention",
public class EntranceEquipment_VersionStructure
    extends AccessEquipment_VersionStructure
{

    protected Boolean door;
    protected Boolean keptOpen;
    protected Boolean revolvingDoor;
    protected Boolean barrier;
    protected BigInteger numberOfGates;
    protected StaffingEnumeration staffing;
    protected Boolean entranceRequiresStaffing;
    protected Boolean entranceRequiresTicket;
    protected Boolean entranceRequiresPassport;
    protected Boolean acousticSensor;
    protected Boolean automaticDoor;
    protected Boolean glassDoor;
    protected Boolean wheelchairPassable;
    protected Boolean wheechairUnaided;
    protected EntranceAttentionEnumeration entranceAttention;
    protected Boolean suitableForCycles;

    public Boolean isDoor() {
        return door;
    }

    public void setDoor(Boolean value) {
        this.door = value;
    }

    public Boolean isKeptOpen() {
        return keptOpen;
    }

    public void setKeptOpen(Boolean value) {
        this.keptOpen = value;
    }

    public Boolean isRevolvingDoor() {
        return revolvingDoor;
    }

    public void setRevolvingDoor(Boolean value) {
        this.revolvingDoor = value;
    }

    public Boolean isBarrier() {
        return barrier;
    }

    public void setBarrier(Boolean value) {
        this.barrier = value;
    }

    public BigInteger getNumberOfGates() {
        return numberOfGates;
    }

    public void setNumberOfGates(BigInteger value) {
        this.numberOfGates = value;
    }

    public StaffingEnumeration getStaffing() {
        return staffing;
    }

    public void setStaffing(StaffingEnumeration value) {
        this.staffing = value;
    }

    public Boolean isEntranceRequiresStaffing() {
        return entranceRequiresStaffing;
    }

    public void setEntranceRequiresStaffing(Boolean value) {
        this.entranceRequiresStaffing = value;
    }

    public Boolean isEntranceRequiresTicket() {
        return entranceRequiresTicket;
    }

    public void setEntranceRequiresTicket(Boolean value) {
        this.entranceRequiresTicket = value;
    }

    public Boolean isEntranceRequiresPassport() {
        return entranceRequiresPassport;
    }

    public void setEntranceRequiresPassport(Boolean value) {
        this.entranceRequiresPassport = value;
    }

    public Boolean isAcousticSensor() {
        return acousticSensor;
    }

    public void setAcousticSensor(Boolean value) {
        this.acousticSensor = value;
    }

    public Boolean isAutomaticDoor() {
        return automaticDoor;
    }

    public void setAutomaticDoor(Boolean value) {
        this.automaticDoor = value;
    }

    public Boolean isGlassDoor() {
        return glassDoor;
    }

    public void setGlassDoor(Boolean value) {
        this.glassDoor = value;
    }

    public Boolean isWheelchairPassable() {
        return wheelchairPassable;
    }

    public void setWheelchairPassable(Boolean value) {
        this.wheelchairPassable = value;
    }

    public Boolean isWheechairUnaided() {
        return wheechairUnaided;
    }

    public void setWheechairUnaided(Boolean value) {
        this.wheechairUnaided = value;
    }

    public EntranceAttentionEnumeration getEntranceAttention() {
        return entranceAttention;
    }

    public void setEntranceAttention(EntranceAttentionEnumeration value) {
        this.entranceAttention = value;
    }

    public Boolean isSuitableForCycles() {
        return suitableForCycles;
    }

    public void setSuitableForCycles(Boolean value) {
        this.suitableForCycles = value;
    }

}
