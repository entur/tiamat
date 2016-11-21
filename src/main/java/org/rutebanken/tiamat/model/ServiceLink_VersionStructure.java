package org.rutebanken.tiamat.model;

public class ServiceLink_VersionStructure
        extends Link_VersionStructure {

    protected ScheduledStopPointRefStructure fromPointRef;
    protected ScheduledStopPointRefStructure toPointRef;
    protected AllModesEnumeration vehicleMode;
    protected OperationalContextRefStructure operationalContextRef;

    public ScheduledStopPointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(ScheduledStopPointRefStructure value) {
        this.fromPointRef = value;
    }

    public ScheduledStopPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(ScheduledStopPointRefStructure value) {
        this.toPointRef = value;
    }

    public AllModesEnumeration getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(AllModesEnumeration value) {
        this.vehicleMode = value;
    }

    public OperationalContextRefStructure getOperationalContextRef() {
        return operationalContextRef;
    }

    public void setOperationalContextRef(OperationalContextRefStructure value) {
        this.operationalContextRef = value;
    }

}
