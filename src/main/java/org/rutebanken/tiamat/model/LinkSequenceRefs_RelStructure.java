package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class LinkSequenceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends LinkSequenceRefStructure>> linkSequenceRef;

    public List<JAXBElement<? extends LinkSequenceRefStructure>> getLinkSequenceRef() {
        if (linkSequenceRef == null) {
            linkSequenceRef = new ArrayList<JAXBElement<? extends LinkSequenceRefStructure>>();
        }
        return this.linkSequenceRef;
    }

}
