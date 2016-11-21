

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class EntranceRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends EntranceRefStructure>> entranceRef;

    public List<JAXBElement<? extends EntranceRefStructure>> getEntranceRef() {
        if (entranceRef == null) {
            entranceRef = new ArrayList<JAXBElement<? extends EntranceRefStructure>>();
        }
        return this.entranceRef;
    }

}
