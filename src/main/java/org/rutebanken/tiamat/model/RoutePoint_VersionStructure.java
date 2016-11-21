

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class RoutePoint_VersionStructure
    extends Point_VersionStructure
{

    protected Boolean viaFlag;
    protected Boolean borderCrossing;

    public Boolean isViaFlag() {
        return viaFlag;
    }

    public void setViaFlag(Boolean value) {
        this.viaFlag = value;
    }

    public Boolean isBorderCrossing() {
        return borderCrossing;
    }

    public void setBorderCrossing(Boolean value) {
        this.borderCrossing = value;
    }

}
