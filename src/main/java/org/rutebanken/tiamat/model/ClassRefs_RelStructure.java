

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class ClassRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends ClassRefStructure>> classRef;

    public List<JAXBElement<? extends ClassRefStructure>> getClassRef() {
        if (classRef == null) {
            classRef = new ArrayList<JAXBElement<? extends ClassRefStructure>>();
        }
    }

}
