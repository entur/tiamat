package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class LineSectionPointMember_VersionStructure
        extends CommonSectionPointMember_VersionedChildStructure {

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
