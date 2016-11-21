

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class GroupOfOperatorsStructure
    extends GroupOfEntities_VersionStructure
{

    protected TransportOrganisationRefs_RelStructure members;

    public TransportOrganisationRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(TransportOrganisationRefs_RelStructure value) {
        this.members = value;
    }

}
