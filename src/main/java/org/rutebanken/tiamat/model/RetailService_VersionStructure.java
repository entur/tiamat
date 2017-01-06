package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class RetailService_VersionStructure
        extends LocalService_VersionStructure {

    protected List<RetailServiceEnumeration> serviceList;

    public List<RetailServiceEnumeration> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<RetailServiceEnumeration>();
        }
        return this.serviceList;
    }

}
