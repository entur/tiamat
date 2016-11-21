

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


public class MoneyService_VersionStructure
    extends LocalService_VersionStructure
{

    protected List<MoneyServiceEnumeration> serviceList;

    public List<MoneyServiceEnumeration> getServiceList() {
        if (serviceList == null) {
            serviceList = new ArrayList<MoneyServiceEnumeration>();
        }
        return this.serviceList;
    }

}
