

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class AccessSummaries_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<AccessSummary> accessSummary;

    public List<AccessSummary> getAccessSummary() {
        if (accessSummary == null) {
            accessSummary = new ArrayList<AccessSummary>();
        }
        return this.accessSummary;
    }

}
