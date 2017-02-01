package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ClassInFrameRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<ClassInFrameRefStructure> classInFrameRef;

    public List<ClassInFrameRefStructure> getClassInFrameRef() {
        if (classInFrameRef == null) {
            classInFrameRef = new ArrayList<ClassInFrameRefStructure>();
        }
        return classInFrameRef;
    }

}
