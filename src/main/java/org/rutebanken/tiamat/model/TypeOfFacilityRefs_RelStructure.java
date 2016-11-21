

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TypeOfFacilityRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TypeOfFacilityRefStructure> typeOfFacilityRef;

    public List<TypeOfFacilityRefStructure> getTypeOfFacilityRef() {
        if (typeOfFacilityRef == null) {
            typeOfFacilityRef = new ArrayList<TypeOfFacilityRefStructure>();
        }
        return this.typeOfFacilityRef;
    }

}
