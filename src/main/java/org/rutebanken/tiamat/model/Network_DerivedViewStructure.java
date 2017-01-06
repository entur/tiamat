package org.rutebanken.tiamat.model;

public class Network_DerivedViewStructure
        extends DerivedViewStructure {

    protected NetworkRefStructure networkRef;
    protected MultilingualStringEntity name;
    protected AllVehicleModesOfTransportEnumeration transportMode;

    public NetworkRefStructure getNetworkRef() {
        return networkRef;
    }

    public void setNetworkRef(NetworkRefStructure value) {
        this.networkRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public AllVehicleModesOfTransportEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(AllVehicleModesOfTransportEnumeration value) {
        this.transportMode = value;
    }

}
