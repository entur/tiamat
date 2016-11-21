

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TopographicPlaceDescriptor_VersionedChildStructure
    extends VersionedChildStructure
{

    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected Qualify qualify;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public Qualify getQualify() {
        return qualify;
    }

    public void setQualify(Qualify value) {
        this.qualify = value;
    }

}
