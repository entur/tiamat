

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class LinkSequenceRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends LinkSequenceRefStructure>> linkSequenceRef;

    public List<JAXBElement<? extends LinkSequenceRefStructure>> getLinkSequenceRef() {
        if (linkSequenceRef == null) {
            linkSequenceRef = new ArrayList<JAXBElement<? extends LinkSequenceRefStructure>>();
        }
        return this.linkSequenceRef;
    }

}
