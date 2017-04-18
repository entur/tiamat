package org.rutebanken.tiamat.model;

import javax.persistence.Transient;
import java.math.BigDecimal;


public class ShelterEquipment_VersionStructure
        extends WaitingEquipment_VersionStructure {

    protected Boolean enclosed;
    @Transient
    protected BigDecimal distanceFromNearestKerb;

    public Boolean isEnclosed() {
        return enclosed;
    }

    public void setEnclosed(Boolean value) {
        this.enclosed = value;
    }

    public BigDecimal getDistanceFromNearestKerb() {
        return distanceFromNearestKerb;
    }

    public void setDistanceFromNearestKerb(BigDecimal value) {
        this.distanceFromNearestKerb = value;
    }

}
