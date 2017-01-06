package org.rutebanken.tiamat.model;

public class ServiceJourneyPattern_VersionStructure
        extends JourneyPattern_VersionStructure {

    protected ServiceJourneyPatternTypeEnumeration serviceJourneyPatternType;

    public ServiceJourneyPatternTypeEnumeration getServiceJourneyPatternType() {
        return serviceJourneyPatternType;
    }

    public void setServiceJourneyPatternType(ServiceJourneyPatternTypeEnumeration value) {
        this.serviceJourneyPatternType = value;
    }

}
