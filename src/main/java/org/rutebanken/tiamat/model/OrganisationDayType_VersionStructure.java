

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class OrganisationDayType_VersionStructure
    extends DayType_VersionStructure
{

    protected Boolean isServiceDay;
    protected ServicedOrganisationRefStructure servicedOrganisationRef;

    public Boolean isIsServiceDay() {
        return isServiceDay;
    }

    public void setIsServiceDay(Boolean value) {
        this.isServiceDay = value;
    }

    public ServicedOrganisationRefStructure getServicedOrganisationRef() {
        return servicedOrganisationRef;
    }

    public void setServicedOrganisationRef(ServicedOrganisationRefStructure value) {
        this.servicedOrganisationRef = value;
    }

}
