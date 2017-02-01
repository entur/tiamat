package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;


public abstract class AccessEquipment_VersionStructure
        extends InstalledEquipment_VersionStructure {

    protected BigDecimal width;
    protected DirectionOfUseEnumeration directionOfUse;
    protected BigInteger passengersPerMinute;
    protected BigInteger relativeWeighting;

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal value) {
        this.width = value;
    }

    public DirectionOfUseEnumeration getDirectionOfUse() {
        return directionOfUse;
    }

    public void setDirectionOfUse(DirectionOfUseEnumeration value) {
        this.directionOfUse = value;
    }

    public BigInteger getPassengersPerMinute() {
        return passengersPerMinute;
    }

    public void setPassengersPerMinute(BigInteger value) {
        this.passengersPerMinute = value;
    }

    public BigInteger getRelativeWeighting() {
        return relativeWeighting;
    }

    public void setRelativeWeighting(BigInteger value) {
        this.relativeWeighting = value;
    }

}
