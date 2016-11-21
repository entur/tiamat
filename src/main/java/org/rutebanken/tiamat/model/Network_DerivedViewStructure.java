

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "networkRef",
    "name",
public class Network_DerivedViewStructure
    extends DerivedViewStructure
{

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
