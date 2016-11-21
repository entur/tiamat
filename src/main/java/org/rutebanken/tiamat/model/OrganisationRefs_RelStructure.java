

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class OrganisationRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends OrganisationRefStructure>> organisationRef;

    public List<JAXBElement<? extends OrganisationRefStructure>> getOrganisationRef() {
        if (organisationRef == null) {
            organisationRef = new ArrayList<JAXBElement<? extends OrganisationRefStructure>>();
        }
        return this.organisationRef;
    }

}
