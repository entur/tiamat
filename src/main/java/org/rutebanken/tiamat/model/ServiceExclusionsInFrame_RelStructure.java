

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ServiceExclusionsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ServiceExclusion> serviceExclusion;

    public List<ServiceExclusion> getServiceExclusion() {
        if (serviceExclusion == null) {
            serviceExclusion = new ArrayList<ServiceExclusion>();
        }
        return this.serviceExclusion;
    }

}
