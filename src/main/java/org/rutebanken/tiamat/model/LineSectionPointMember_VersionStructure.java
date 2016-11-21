

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


public class LineSectionPointMember_VersionStructure
    extends CommonSectionPointMember_VersionedChildStructure
{

    protected LineSectionPointTypeEnumeration lineSectionPointType;
    protected Boolean showAsAccessible;
    protected List<VehicleModeEnumeration> connectingVehicleModes;

    public LineSectionPointTypeEnumeration getLineSectionPointType() {
        return lineSectionPointType;
    }

    public void setLineSectionPointType(LineSectionPointTypeEnumeration value) {
        this.lineSectionPointType = value;
    }

    public Boolean isShowAsAccessible() {
        return showAsAccessible;
    }

    public void setShowAsAccessible(Boolean value) {
        this.showAsAccessible = value;
    }

    public List<VehicleModeEnumeration> getConnectingVehicleModes() {
        if (connectingVehicleModes == null) {
            connectingVehicleModes = new ArrayList<VehicleModeEnumeration>();
        }
        return this.connectingVehicleModes;
    }

}
