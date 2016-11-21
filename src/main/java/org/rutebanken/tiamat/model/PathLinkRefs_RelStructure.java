

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class PathLinkRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<Object> pathLinkRefOrPathLinkRefByValue;

    public List<Object> getPathLinkRefOrPathLinkRefByValue() {
        if (pathLinkRefOrPathLinkRefByValue == null) {
            pathLinkRefOrPathLinkRefByValue = new ArrayList<Object>();
        }
        return this.pathLinkRefOrPathLinkRefByValue;
    }

}
