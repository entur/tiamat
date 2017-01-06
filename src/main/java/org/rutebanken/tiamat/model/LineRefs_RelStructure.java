package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class LineRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends LineRefStructure>> lineRef;

    public List<JAXBElement<? extends LineRefStructure>> getLineRef() {
        if (lineRef == null) {
            lineRef = new ArrayList<JAXBElement<? extends LineRefStructure>>();
        }
        return this.lineRef;
    }

}
