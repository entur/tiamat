

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class WheelchairVehicleEquipment_VersionStructure
    extends ActualVehicleEquipment_VersionStructure
{

    protected Boolean hasWheelChairSpaces;
    protected BigInteger numberOfWheelchairAreas;
    protected BigDecimal widthOfAccessArea;
    protected BigDecimal lengthOfAccessArea;
    protected BigDecimal heightOfAccessArea;
    protected BigDecimal wheelchairTurningCircle;
    protected Boolean companionSeat;

    public Boolean isHasWheelChairSpaces() {
        return hasWheelChairSpaces;
    }

    public void setHasWheelChairSpaces(Boolean value) {
        this.hasWheelChairSpaces = value;
    }

    public BigInteger getNumberOfWheelchairAreas() {
        return numberOfWheelchairAreas;
    }

    public void setNumberOfWheelchairAreas(BigInteger value) {
        this.numberOfWheelchairAreas = value;
    }

    public BigDecimal getWidthOfAccessArea() {
        return widthOfAccessArea;
    }

    public void setWidthOfAccessArea(BigDecimal value) {
        this.widthOfAccessArea = value;
    }

    public BigDecimal getLengthOfAccessArea() {
        return lengthOfAccessArea;
    }

    public void setLengthOfAccessArea(BigDecimal value) {
        this.lengthOfAccessArea = value;
    }

    public BigDecimal getHeightOfAccessArea() {
        return heightOfAccessArea;
    }

    public void setHeightOfAccessArea(BigDecimal value) {
        this.heightOfAccessArea = value;
    }

    public BigDecimal getWheelchairTurningCircle() {
        return wheelchairTurningCircle;
    }

    public void setWheelchairTurningCircle(BigDecimal value) {
        this.wheelchairTurningCircle = value;
    }

    public Boolean isCompanionSeat() {
        return companionSeat;
    }

    public void setCompanionSeat(Boolean value) {
        this.companionSeat = value;
    }

}
