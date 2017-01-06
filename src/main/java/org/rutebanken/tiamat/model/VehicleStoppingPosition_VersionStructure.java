package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class VehicleStoppingPosition_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity stoppingPositionName;
    protected MultilingualStringEntity label;
    protected BigInteger bearing;
    protected VehiclePositionAlignments_RelStructure vehiclePositionAlignments;

    public MultilingualStringEntity getStoppingPositionName() {
        return stoppingPositionName;
    }

    public void setStoppingPositionName(MultilingualStringEntity value) {
        this.stoppingPositionName = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public BigInteger getBearing() {
        return bearing;
    }

    public void setBearing(BigInteger value) {
        this.bearing = value;
    }

    public VehiclePositionAlignments_RelStructure getVehiclePositionAlignments() {
        return vehiclePositionAlignments;
    }

    public void setVehiclePositionAlignments(VehiclePositionAlignments_RelStructure value) {
        this.vehiclePositionAlignments = value;
    }

}
