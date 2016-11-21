

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "transportOrganisationRef",
public class Network_VersionStructure
    extends GroupOfLines_VersionStructure
{

    protected JAXBElement<? extends OrganisationRefStructure> transportOrganisationRef;
    protected GroupsOfLinesInFrame_RelStructure groupsOfLines;

    public JAXBElement<? extends OrganisationRefStructure> getTransportOrganisationRef() {
        return transportOrganisationRef;
    }

    public void setTransportOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.transportOrganisationRef = value;
    }

    public GroupsOfLinesInFrame_RelStructure getGroupsOfLines() {
        return groupsOfLines;
    }

    public void setGroupsOfLines(GroupsOfLinesInFrame_RelStructure value) {
        this.groupsOfLines = value;
    }

}
