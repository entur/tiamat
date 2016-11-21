package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Relationships {

    protected List<ClassRelationshipInFrameStructure> classRelationshipInFrame;

    public List<ClassRelationshipInFrameStructure> getClassRelationshipInFrame() {
        if (classRelationshipInFrame == null) {
            classRelationshipInFrame = new ArrayList<ClassRelationshipInFrameStructure>();
        }
        return classRelationshipInFrame;
    }

}
