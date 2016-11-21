package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TypesOfFrame_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<Object> typeOfFrameRefOrTypeOfFrame;

    public List<Object> getTypeOfFrameRefOrTypeOfFrame() {
        if (typeOfFrameRefOrTypeOfFrame == null) {
            typeOfFrameRefOrTypeOfFrame = new ArrayList<Object>();
        }
        return this.typeOfFrameRefOrTypeOfFrame;
    }

}
