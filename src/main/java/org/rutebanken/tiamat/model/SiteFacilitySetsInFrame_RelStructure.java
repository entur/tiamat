

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class SiteFacilitySetsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<SiteFacilitySet> siteFacilitySet;

    public List<SiteFacilitySet> getSiteFacilitySet() {
        if (siteFacilitySet == null) {
            siteFacilitySet = new ArrayList<SiteFacilitySet>();
        }
        return this.siteFacilitySet;
    }

}
