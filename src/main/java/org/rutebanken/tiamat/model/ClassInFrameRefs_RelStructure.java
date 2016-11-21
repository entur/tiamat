

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ClassInFrameRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<ClassInFrameRefStructure> classInFrameRef;

    public List<ClassInFrameRefStructure> getClassInFrameRef() {
        if (classInFrameRef == null) {
            classInFrameRef = new ArrayList<ClassInFrameRefStructure>();
        }
    }

}
