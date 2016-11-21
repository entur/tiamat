

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


public class LinkRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<?>> linkRefOrLinkRefByValue;

    public List<JAXBElement<?>> getLinkRefOrLinkRefByValue() {
        if (linkRefOrLinkRefByValue == null) {
            linkRefOrLinkRefByValue = new ArrayList<JAXBElement<?>>();
        }
        return this.linkRefOrLinkRefByValue;
    }

}
