

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class Organisations {

    protected List<JAXBElement<? extends DataManagedObjectStructure>> organisation_;

    public List<JAXBElement<? extends DataManagedObjectStructure>> getOrganisation_() {
        if (organisation_ == null) {
            organisation_ = new ArrayList<JAXBElement<? extends DataManagedObjectStructure>>();
        }
        return this.organisation_;
    }

}
