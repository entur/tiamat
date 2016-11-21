

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public abstract class InfrastructureLinkRestriction_VersionStructure
    extends NetworkRestriction_VersionStructure
{

    protected LinkRefStructure fromLinkRef;
    protected LinkRefStructure toLinkRef;

    public LinkRefStructure getFromLinkRef() {
        return fromLinkRef;
    }

    public void setFromLinkRef(LinkRefStructure value) {
        this.fromLinkRef = value;
    }

    public LinkRefStructure getToLinkRef() {
        return toLinkRef;
    }

    public void setToLinkRef(LinkRefStructure value) {
        this.toLinkRef = value;
    }

}
