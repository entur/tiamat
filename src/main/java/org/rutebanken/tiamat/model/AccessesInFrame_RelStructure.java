

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class AccessesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Access> access;

    public List<Access> getAccess() {
        if (access == null) {
            access = new ArrayList<Access>();
        }
        return this.access;
    }

}
