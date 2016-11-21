

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class HireService_VersionStructure
    extends LocalService_VersionStructure
{

    protected List<JAXBElement<List<HireServiceEnumeration>>> serviceList;

    public List<JAXBElement<List<HireServiceEnumeration>>> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<JAXBElement<List<HireServiceEnumeration>>>();
        }
        return this.serviceList;
    }

}
