package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Attributes {

    protected List<ClassAttributeInFrame> classAttributeInFrame;

    public List<ClassAttributeInFrame> getClassAttributeInFrame() {
        if (classAttributeInFrame == null) {
            classAttributeInFrame = new ArrayList<ClassAttributeInFrame>();
        }
        return classAttributeInFrame;
    }

}
