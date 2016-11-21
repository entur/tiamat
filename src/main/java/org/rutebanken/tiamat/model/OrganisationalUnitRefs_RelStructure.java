package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OrganisationalUnitRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<OrganisationalUnitRefStructure> organisationalUnitRef;

    public List<OrganisationalUnitRefStructure> getOrganisationalUnitRef() {
        if (organisationalUnitRef == null) {
            organisationalUnitRef = new ArrayList<OrganisationalUnitRefStructure>();
        }
        return this.organisationalUnitRef;
    }

}
