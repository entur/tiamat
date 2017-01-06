package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class LineSection_VersionStructure
        extends CommonSection_VersionStructure {

    protected CommonSectionPointMembers_RelStructure reverseMembers;
    protected SectionTypeEnumeration sectionType;
    protected JAXBElement<? extends LineRefStructure> lineRef;
    protected JAXBElement<? extends OrganisationRefStructure> transportOrganisationRef;

    public CommonSectionPointMembers_RelStructure getReverseMembers() {
        return reverseMembers;
    }

    public void setReverseMembers(CommonSectionPointMembers_RelStructure value) {
        this.reverseMembers = value;
    }

    public SectionTypeEnumeration getSectionType() {
        return sectionType;
    }

    public void setSectionType(SectionTypeEnumeration value) {
        this.sectionType = value;
    }

    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    public JAXBElement<? extends OrganisationRefStructure> getTransportOrganisationRef() {
        return transportOrganisationRef;
    }

    public void setTransportOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.transportOrganisationRef = value;
    }

}
