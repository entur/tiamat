

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TimebandsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Timeband> timeband;

    public List<Timeband> getTimeband() {
        if (timeband == null) {
            timeband = new ArrayList<Timeband>();
        }
        return this.timeband;
    }

}
