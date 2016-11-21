

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


public class ModeRefStructure
    extends SubmodeRefStructure
{

    protected AllVehicleModesOfTransportEnumeration mode;

    public AllVehicleModesOfTransportEnumeration getMode() {
        return mode;
    }

    public void setMode(AllVehicleModesOfTransportEnumeration value) {
        this.mode = value;
    }

}
