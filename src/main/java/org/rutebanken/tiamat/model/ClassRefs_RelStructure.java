package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class ClassRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends ClassRefStructure>> classRef;

    public List<JAXBElement<? extends ClassRefStructure>> getClassRef() {
        if (classRef == null) {
            classRef = new ArrayList<JAXBElement<? extends ClassRefStructure>>();
        }
        return classRef;
    }

}
