package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class OrganisationRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends OrganisationRefStructure>> organisationRef;

    public List<JAXBElement<? extends OrganisationRefStructure>> getOrganisationRef() {
        if (organisationRef == null) {
            organisationRef = new ArrayList<JAXBElement<? extends OrganisationRefStructure>>();
        }
        return this.organisationRef;
    }

}
