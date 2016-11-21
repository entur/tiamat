

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "from",
public class Access_VersionStructure
    extends Transfer_VersionStructure
{

    protected AccessEndStructure from;
    protected AccessEndStructure to;

    public AccessEndStructure getFrom() {
        return from;
    }

    public void setFrom(AccessEndStructure value) {
        this.from = value;
    }

    public AccessEndStructure getTo() {
        return to;
    }

    public void setTo(AccessEndStructure value) {
        this.to = value;
    }

}
