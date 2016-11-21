

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PurposesOfGroupingInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PurposeOfGrouping> purposeOfGrouping;

    public List<PurposeOfGrouping> getPurposeOfGrouping() {
        if (purposeOfGrouping == null) {
            purposeOfGrouping = new ArrayList<PurposeOfGrouping>();
        }
        return this.purposeOfGrouping;
    }

}
