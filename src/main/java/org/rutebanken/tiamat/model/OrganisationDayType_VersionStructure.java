package org.rutebanken.tiamat.model;

public class OrganisationDayType_VersionStructure
        extends DayType_VersionStructure {

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
