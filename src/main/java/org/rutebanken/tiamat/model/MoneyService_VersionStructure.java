package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class MoneyService_VersionStructure
        extends LocalService_VersionStructure {

    protected List<MoneyServiceEnumeration> serviceList;

    public List<MoneyServiceEnumeration> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<MoneyServiceEnumeration>();
        }
        return this.serviceList;
    }

}
