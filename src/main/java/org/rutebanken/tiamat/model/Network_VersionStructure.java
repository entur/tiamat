package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class Network_VersionStructure
        extends GroupOfLines_VersionStructure {

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
