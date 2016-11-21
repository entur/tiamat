

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class EntityInVersionInFrameRefStructure
    extends VersionOfObjectRefStructure
{

    protected ModificationEnumeration modification;
    protected String id;

    public ModificationEnumeration getModification() {
        if (modification == null) {
            return ModificationEnumeration.NEW;
        } else {
            return modification;
        }
    }

    public void setModification(ModificationEnumeration value) {
        this.modification = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}
