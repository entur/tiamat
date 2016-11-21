

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ServiceFacilitySetsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<ServiceFacilitySet> serviceFacilitySet;

    public List<ServiceFacilitySet> getServiceFacilitySet() {
        if (serviceFacilitySet == null) {
            serviceFacilitySet = new ArrayList<ServiceFacilitySet>();
        }
        return this.serviceFacilitySet;
    }

}
