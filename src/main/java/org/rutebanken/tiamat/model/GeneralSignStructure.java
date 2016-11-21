

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class GeneralSignStructure
    extends SignEquipment_VersionStructure
{

    protected MultilingualStringEntity content;
    protected SignContentEnumeration signContentType;

    public MultilingualStringEntity getContent() {
        return content;
    }

    public void setContent(MultilingualStringEntity value) {
        this.content = value;
    }

    public SignContentEnumeration getSignContentType() {
        return signContentType;
    }

    public void setSignContentType(SignContentEnumeration value) {
        this.signContentType = value;
    }

}
