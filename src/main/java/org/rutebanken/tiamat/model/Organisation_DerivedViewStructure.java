package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class Organisation_DerivedViewStructure
        extends DerivedViewStructure {

    protected JAXBElement<? extends OrganisationRefStructure> organisationRef;
    protected MultilingualStringEntity name;
    protected MultilingualStringEntity shortName;
    protected MultilingualStringEntity legalName;
    protected MultilingualStringEntity tradingName;
    protected AlternativeNames_RelStructure alternativeNames;
    protected ContactStructure contactDetails;

    public JAXBElement<? extends OrganisationRefStructure> getOrganisationRef() {
        return organisationRef;
    }

    public void setOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.organisationRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getLegalName() {
        return legalName;
    }

    public void setLegalName(MultilingualStringEntity value) {
        this.legalName = value;
    }

    public MultilingualStringEntity getTradingName() {
        return tradingName;
    }

    public void setTradingName(MultilingualStringEntity value) {
        this.tradingName = value;
    }

    public AlternativeNames_RelStructure getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(AlternativeNames_RelStructure value) {
        this.alternativeNames = value;
    }

    public ContactStructure getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactStructure value) {
        this.contactDetails = value;
    }

}
