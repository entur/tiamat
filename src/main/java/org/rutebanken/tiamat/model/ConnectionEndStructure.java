

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


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
