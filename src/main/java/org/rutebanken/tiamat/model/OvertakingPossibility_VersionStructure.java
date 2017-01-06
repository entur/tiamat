package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public class OvertakingPossibility_VersionStructure
        extends NetworkRestriction_VersionStructure {

    protected BigDecimal overtakingWidth;
    protected LinkRefStructure overtakingOnLinkRef;
    protected PointRefStructure overtakingAtPointRef;
    protected VehicleTypeRefStructure overtakingVehicleTypeRef;
    protected VehicleTypeRefStructure overtakenVehicleTypeRef;

    public BigDecimal getOvertakingWidth() {
        return overtakingWidth;
    }

    public void setOvertakingWidth(BigDecimal value) {
        this.overtakingWidth = value;
    }

    public LinkRefStructure getOvertakingOnLinkRef() {
        return overtakingOnLinkRef;
    }

    public void setOvertakingOnLinkRef(LinkRefStructure value) {
        this.overtakingOnLinkRef = value;
    }

    public PointRefStructure getOvertakingAtPointRef() {
        return overtakingAtPointRef;
    }

    public void setOvertakingAtPointRef(PointRefStructure value) {
        this.overtakingAtPointRef = value;
    }

    public VehicleTypeRefStructure getOvertakingVehicleTypeRef() {
        return overtakingVehicleTypeRef;
    }

    public void setOvertakingVehicleTypeRef(VehicleTypeRefStructure value) {
        this.overtakingVehicleTypeRef = value;
    }

    public VehicleTypeRefStructure getOvertakenVehicleTypeRef() {
        return overtakenVehicleTypeRef;
    }

    public void setOvertakenVehicleTypeRef(VehicleTypeRefStructure value) {
        this.overtakenVehicleTypeRef = value;
    }

}
