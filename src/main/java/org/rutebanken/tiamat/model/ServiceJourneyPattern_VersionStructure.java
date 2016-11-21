

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ServiceJourneyPattern_VersionStructure
    extends JourneyPattern_VersionStructure
{

    protected ServiceJourneyPatternTypeEnumeration serviceJourneyPatternType;

    public ServiceJourneyPatternTypeEnumeration getServiceJourneyPatternType() {
        return serviceJourneyPatternType;
    }

    public void setServiceJourneyPatternType(ServiceJourneyPatternTypeEnumeration value) {
        this.serviceJourneyPatternType = value;
    }

}
