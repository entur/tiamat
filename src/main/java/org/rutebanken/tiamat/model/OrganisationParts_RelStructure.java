package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class OrganisationParts_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> organisationPartRefOrOrganisationPart_;

    public List<JAXBElement<?>> getOrganisationPartRefOrOrganisationPart_() {
        if (organisationPartRefOrOrganisationPart_ == null) {
            organisationPartRefOrOrganisationPart_ = new ArrayList<JAXBElement<?>>();
        }
        return this.organisationPartRefOrOrganisationPart_;
    }

}
