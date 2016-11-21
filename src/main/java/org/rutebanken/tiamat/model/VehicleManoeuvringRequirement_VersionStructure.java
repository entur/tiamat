package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public class VehicleManoeuvringRequirement_VersionStructure
        extends VehicleRequirement_VersionStructure {

    protected Boolean reversible;
    protected BigDecimal minimumTurningCircle;
    protected BigDecimal minimumOvertakingWidth;
    protected BigDecimal minimumLength;

    public Boolean isReversible() {
        return reversible;
    }

    public void setReversible(Boolean value) {
        this.reversible = value;
    }

    public BigDecimal getMinimumTurningCircle() {
        return minimumTurningCircle;
    }

    public void setMinimumTurningCircle(BigDecimal value) {
        this.minimumTurningCircle = value;
    }

    public BigDecimal getMinimumOvertakingWidth() {
        return minimumOvertakingWidth;
    }

    public void setMinimumOvertakingWidth(BigDecimal value) {
        this.minimumOvertakingWidth = value;
    }

    public BigDecimal getMinimumLength() {
        return minimumLength;
    }

    public void setMinimumLength(BigDecimal value) {
        this.minimumLength = value;
    }

}
