package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CateringService_VersionStructure
        extends LocalService_VersionStructure {

    protected List<CateringServiceEnumeration> serviceList;

    public List<CateringServiceEnumeration> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<CateringServiceEnumeration>();
        }
        return this.serviceList;
    }

}
