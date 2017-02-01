package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class LinkRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<?>> linkRefOrLinkRefByValue;

    public List<JAXBElement<?>> getLinkRefOrLinkRefByValue() {
        if (linkRefOrLinkRefByValue == null) {
            linkRefOrLinkRefByValue = new ArrayList<JAXBElement<?>>();
        }
        return this.linkRefOrLinkRefByValue;
    }

}
