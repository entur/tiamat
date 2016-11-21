

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
