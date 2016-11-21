

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class SiteRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends SiteRefStructure>> siteRef;

    public List<JAXBElement<? extends SiteRefStructure>> getSiteRef() {
        if (siteRef == null) {
            siteRef = new ArrayList<JAXBElement<? extends SiteRefStructure>>();
        }
        return this.siteRef;
    }

}
