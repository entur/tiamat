

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class OneToManyRelationshipStructure
    extends RelationshipStructure
{

    protected ModificationSetEnumeration modificationSet;

    public ModificationSetEnumeration getModificationSet() {
        if (modificationSet == null) {
            return ModificationSetEnumeration.ALL;
        } else {
            return modificationSet;
        }
    }

    public void setModificationSet(ModificationSetEnumeration value) {
        this.modificationSet = value;
    }

}
