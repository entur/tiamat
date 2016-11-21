

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TypeOfOrganisationRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TypeOfOrganisationRefStructure> typeOfOrganisationRef;

    public List<TypeOfOrganisationRefStructure> getTypeOfOrganisationRef() {
        if (typeOfOrganisationRef == null) {
            typeOfOrganisationRef = new ArrayList<TypeOfOrganisationRefStructure>();
        }
        return this.typeOfOrganisationRef;
    }

}
