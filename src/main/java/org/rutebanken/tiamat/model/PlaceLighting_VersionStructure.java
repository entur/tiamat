

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "lighting",
public class PlaceLighting_VersionStructure
    extends AccessEquipment_VersionStructure
{

    protected LightingEnumeration lighting;
    protected Boolean alwaysLit;

    public LightingEnumeration getLighting() {
        return lighting;
    }

    public void setLighting(LightingEnumeration value) {
        this.lighting = value;
    }

    public Boolean isAlwaysLit() {
        return alwaysLit;
    }

    public void setAlwaysLit(Boolean value) {
        this.alwaysLit = value;
    }

}
