package org.rutebanken.tiamat.model;

public class ConnectionEndStructure {

    protected AllVehicleModesOfTransportEnumeration transportMode;
    protected ScheduledStopPointRefStructure scheduledStopPointRef;

    public AllVehicleModesOfTransportEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(AllVehicleModesOfTransportEnumeration value) {
        this.transportMode = value;
    }

    public ScheduledStopPointRefStructure getScheduledStopPointRef() {
        return scheduledStopPointRef;
    }

    public void setScheduledStopPointRef(ScheduledStopPointRefStructure value) {
        this.scheduledStopPointRef = value;
    }

}
