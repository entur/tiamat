

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "externalConnectionLinkRef",
    "from",
    "to",
public class Connection_VersionStructure
    extends Transfer_VersionStructure
{

    protected ExternalObjectRefStructure externalConnectionLinkRef;
    protected ConnectionEndStructure from;
    protected ConnectionEndStructure to;
    protected Boolean transferOnly;

    public ExternalObjectRefStructure getExternalConnectionLinkRef() {
        return externalConnectionLinkRef;
    }

    public void setExternalConnectionLinkRef(ExternalObjectRefStructure value) {
        this.externalConnectionLinkRef = value;
    }

    public ConnectionEndStructure getFrom() {
        return from;
    }

    public void setFrom(ConnectionEndStructure value) {
        this.from = value;
    }

    public ConnectionEndStructure getTo() {
        return to;
    }

    public void setTo(ConnectionEndStructure value) {
        this.to = value;
    }

    public Boolean isTransferOnly() {
        return transferOnly;
    }

    public void setTransferOnly(Boolean value) {
        this.transferOnly = value;
    }

}
