

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class OrganisationalUnitRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<OrganisationalUnitRefStructure> organisationalUnitRef;

    public List<OrganisationalUnitRefStructure> getOrganisationalUnitRef() {
        if (organisationalUnitRef == null) {
            organisationalUnitRef = new ArrayList<OrganisationalUnitRefStructure>();
        }
        return this.organisationalUnitRef;
    }

}
