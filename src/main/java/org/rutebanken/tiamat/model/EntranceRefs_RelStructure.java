package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class EntranceRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends EntranceRefStructure>> entranceRef;

    public List<JAXBElement<? extends EntranceRefStructure>> getEntranceRef() {
        if (entranceRef == null) {
            entranceRef = new ArrayList<JAXBElement<? extends EntranceRefStructure>>();
        }
        return this.entranceRef;
    }

}
