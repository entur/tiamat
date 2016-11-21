

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TypeOfFrameRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TypeOfFrameRefStructure> typeOfFrameRef;

    public List<TypeOfFrameRefStructure> getTypeOfFrameRef() {
        if (typeOfFrameRef == null) {
            typeOfFrameRef = new ArrayList<TypeOfFrameRefStructure>();
        }
        return this.typeOfFrameRef;
    }

}
