package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class TransportOrganisationRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends OrganisationRefStructure>> transportOrganisationRef;

    public List<JAXBElement<? extends OrganisationRefStructure>> getTransportOrganisationRef() {
        if (transportOrganisationRef == null) {
            transportOrganisationRef = new ArrayList<JAXBElement<? extends OrganisationRefStructure>>();
        }
        return this.transportOrganisationRef;
    }

}
