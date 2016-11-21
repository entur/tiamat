

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TimeDemandTypeAssignment_VersionStructure
    extends Assignment_VersionStructure
{

    protected TimeDemandTypeRefStructure timeDemandTypeRef;
    protected TimebandRefStructure timebandRef;
    protected GroupOfTimingLinksRefStructure groupOfTimingLinksRef;

    public TimeDemandTypeRefStructure getTimeDemandTypeRef() {
        return timeDemandTypeRef;
    }

    public void setTimeDemandTypeRef(TimeDemandTypeRefStructure value) {
        this.timeDemandTypeRef = value;
    }

    public TimebandRefStructure getTimebandRef() {
        return timebandRef;
    }

    public void setTimebandRef(TimebandRefStructure value) {
        this.timebandRef = value;
    }

    public GroupOfTimingLinksRefStructure getGroupOfTimingLinksRef() {
        return groupOfTimingLinksRef;
    }

    public void setGroupOfTimingLinksRef(GroupOfTimingLinksRefStructure value) {
        this.groupOfTimingLinksRef = value;
    }

}
