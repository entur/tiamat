

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public abstract class NetworkRestriction_VersionStructure
    extends Assignment_VersionStructure
{

    protected boolean restricted;

    public boolean isRestricted() {
        return restricted;
    }

    public void setRestricted(boolean value) {
        this.restricted = value;
    }

}
