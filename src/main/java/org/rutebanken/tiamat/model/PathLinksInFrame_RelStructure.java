

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PathLinksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PathLink> pathLink;

    public List<PathLink> getPathLink() {
        if (pathLink == null) {
            pathLink = new ArrayList<PathLink>();
        }
        return this.pathLink;
    }

}
