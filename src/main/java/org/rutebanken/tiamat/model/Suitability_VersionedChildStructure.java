

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class Suitability_VersionedChildStructure
    extends UserNeed_VersionedChildStructure
{

    protected SuitableEnumeration suitable;

    public SuitableEnumeration getSuitable() {
        return suitable;
    }

    public void setSuitable(SuitableEnumeration value) {
        this.suitable = value;
    }

}
