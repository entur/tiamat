package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class OrganisationsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends DataManagedObjectStructure>> organisation_;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getOrganisation_() {
        if (organisation_ == null) {
            organisation_ = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.organisation_;
    }

}
