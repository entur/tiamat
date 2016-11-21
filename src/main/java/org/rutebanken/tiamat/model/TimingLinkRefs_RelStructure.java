

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class TimingLinkRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<Object> timingLinkRefOrTimingLinkRefByValue;

    public List<Object> getTimingLinkRefOrTimingLinkRefByValue() {
        if (timingLinkRefOrTimingLinkRefByValue == null) {
            timingLinkRefOrTimingLinkRefByValue = new ArrayList<Object>();
        }
        return this.timingLinkRefOrTimingLinkRefByValue;
    }

}
