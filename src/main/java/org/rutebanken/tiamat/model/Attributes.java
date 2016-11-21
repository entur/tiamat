

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class Attributes {

    protected List<ClassAttributeInFrame> classAttributeInFrame;

    public List<ClassAttributeInFrame> getClassAttributeInFrame() {
        if (classAttributeInFrame == null) {
            classAttributeInFrame = new ArrayList<ClassAttributeInFrame>();
        }
        return classAttributeInFrame;
    }

}
