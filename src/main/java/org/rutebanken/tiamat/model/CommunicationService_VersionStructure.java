package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CommunicationService_VersionStructure
        extends LocalService_VersionStructure {

    protected List<CommunicationServiceEnumeration> serviceList;

    public List<CommunicationServiceEnumeration> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<CommunicationServiceEnumeration>();
        }
        return this.serviceList;
    }

}
