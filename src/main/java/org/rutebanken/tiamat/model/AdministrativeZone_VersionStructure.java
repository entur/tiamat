package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class AdministrativeZone_VersionStructure
        extends Zone_VersionStructure {

    protected JAXBElement<? extends OrganisationRefStructure> organisationRef;
    protected ResponsibilitySets_RelStructure responsibilities;
    protected CodespaceAssignments_RelStructure codespaceAssignments;

    public JAXBElement<? extends OrganisationRefStructure> getOrganisationRef() {
        return organisationRef;
    }

    public void setOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.organisationRef = value;
    }

    public ResponsibilitySets_RelStructure getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(ResponsibilitySets_RelStructure value) {
        this.responsibilities = value;
    }

    public CodespaceAssignments_RelStructure getCodespaceAssignments() {
        return codespaceAssignments;
    }

    public void setCodespaceAssignments(CodespaceAssignments_RelStructure value) {
        this.codespaceAssignments = value;
    }

}
