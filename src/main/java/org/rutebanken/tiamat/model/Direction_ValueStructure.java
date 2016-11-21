

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class Direction_ValueStructure
    extends TypeOfValue_VersionStructure
{

    protected ExternalObjectRefStructure externalDirectionRef;
    protected DirectionTypeEnumeration directionType;
    protected DirectionRefStructure oppositeDIrectionRef;

    public ExternalObjectRefStructure getExternalDirectionRef() {
        return externalDirectionRef;
    }

    public void setExternalDirectionRef(ExternalObjectRefStructure value) {
        this.externalDirectionRef = value;
    }

    public DirectionTypeEnumeration getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionTypeEnumeration value) {
        this.directionType = value;
    }

    public DirectionRefStructure getOppositeDIrectionRef() {
        return oppositeDIrectionRef;
    }

    public void setOppositeDIrectionRef(DirectionRefStructure value) {
        this.oppositeDIrectionRef = value;
    }

}
