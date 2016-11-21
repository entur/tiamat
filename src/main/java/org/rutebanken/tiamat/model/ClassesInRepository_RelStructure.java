package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ClassesInRepository_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<Object> classInFrameRefOrClassInFrame;

    public List<Object> getClassInFrameRefOrClassInFrame() {
        if (classInFrameRefOrClassInFrame == null) {
            classInFrameRefOrClassInFrame = new ArrayList<Object>();
        }
        return classInFrameRefOrClassInFrame;
    }

}
