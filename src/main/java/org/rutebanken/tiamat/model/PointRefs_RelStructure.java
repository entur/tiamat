package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class PointRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends PointRefStructure>> pointRef;

    public List<JAXBElement<? extends PointRefStructure>> getPointRef() {
        if (pointRef == null) {
            pointRef = new ArrayList<JAXBElement<? extends PointRefStructure>>();
        }
        return this.pointRef;
    }

}
