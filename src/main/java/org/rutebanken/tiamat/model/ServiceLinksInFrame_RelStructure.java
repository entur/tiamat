

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ServiceLinksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ServiceLink> serviceLink;

    public List<ServiceLink> getServiceLink() {
        if (serviceLink == null) {
            serviceLink = new ArrayList<ServiceLink>();
        }
        return this.serviceLink;
    }

}
