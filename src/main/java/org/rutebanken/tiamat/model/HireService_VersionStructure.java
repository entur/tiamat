package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class HireService_VersionStructure
        extends LocalService_VersionStructure {

    protected List<JAXBElement<List<HireServiceEnumeration>>> serviceList;

    public List<JAXBElement<List<HireServiceEnumeration>>> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<JAXBElement<List<HireServiceEnumeration>>>();
        }
        return this.serviceList;
    }

}
