

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class LinkTypeRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TypeOfLinkRefStructure> typeOfLinkRef;

    public List<TypeOfLinkRefStructure> getTypeOfLinkRef() {
        if (typeOfLinkRef == null) {
            typeOfLinkRef = new ArrayList<TypeOfLinkRefStructure>();
        }
        return this.typeOfLinkRef;
    }

}
