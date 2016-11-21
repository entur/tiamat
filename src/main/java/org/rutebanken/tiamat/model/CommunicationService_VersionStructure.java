

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


public class CommunicationService_VersionStructure
    extends LocalService_VersionStructure
{

    protected List<CommunicationServiceEnumeration> serviceList;

    public List<CommunicationServiceEnumeration> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<CommunicationServiceEnumeration>();
        }
        return this.serviceList;
    }

}
