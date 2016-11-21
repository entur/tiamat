

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public abstract class PassengerEquipment_VersionStructure
    extends InstalledEquipment_VersionStructure
{

    protected Boolean fixed;

    public Boolean isFixed() {
        return fixed;
    }

    public void setFixed(Boolean value) {
        this.fixed = value;
    }

}
