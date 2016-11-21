

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TypeOfPlaceRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TypeOfPlaceRefStructure> typeOfPlaceRef;

    public List<TypeOfPlaceRefStructure> getTypeOfPlaceRef() {
        if (typeOfPlaceRef == null) {
            typeOfPlaceRef = new ArrayList<TypeOfPlaceRefStructure>();
        }
        return this.typeOfPlaceRef;
    }

}
