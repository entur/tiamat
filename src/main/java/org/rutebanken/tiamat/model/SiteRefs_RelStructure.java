package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class SiteRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends SiteRefStructure>> siteRef;

    public List<JAXBElement<? extends SiteRefStructure>> getSiteRef() {
        if (siteRef == null) {
            siteRef = new ArrayList<JAXBElement<? extends SiteRefStructure>>();
        }
        return this.siteRef;
    }

}
