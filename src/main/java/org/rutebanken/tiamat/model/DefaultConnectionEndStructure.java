package org.rutebanken.tiamat.model;

public class DefaultConnectionEndStructure {

    protected VehicleModeEnumeration transportMode;
    protected OperatorView operatorView;

    public VehicleModeEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(VehicleModeEnumeration value) {
        this.transportMode = value;
    }

    public OperatorView getOperatorView() {
        return operatorView;
    }

    public void setOperatorView(OperatorView value) {
        this.operatorView = value;
    }

}
