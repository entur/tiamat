

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TransportAdministrativeZone_VersionStructure
    extends AdministrativeZone_VersionStructure
{

    protected List<AllVehicleModesOfTransportEnumeration> vehicleModes;

    public List<AllVehicleModesOfTransportEnumeration> getVehicleModes() {
        if (vehicleModes == null) {
            vehicleModes = new ArrayList<AllVehicleModesOfTransportEnumeration>();
        }
        return this.vehicleModes;
    }

}
