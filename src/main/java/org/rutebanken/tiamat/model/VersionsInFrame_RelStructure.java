

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class VersionsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Version> version;

    public List<Version> getVersion() {
        if (version == null) {
            version = new ArrayList<Version>();
        }
        return this.version;
    }

}
