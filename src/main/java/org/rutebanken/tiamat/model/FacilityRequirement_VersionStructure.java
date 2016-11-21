

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class FacilityRequirement_VersionStructure
    extends VehicleRequirement_VersionStructure
{

    protected ServiceFacilitySets_RelStructure facilitySets;

    public ServiceFacilitySets_RelStructure getFacilitySets() {
        return facilitySets;
    }

    public void setFacilitySets(ServiceFacilitySets_RelStructure value) {
        this.facilitySets = value;
    }

}
