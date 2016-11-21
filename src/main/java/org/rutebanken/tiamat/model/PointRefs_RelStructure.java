

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class PointRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends PointRefStructure>> pointRef;

    public List<JAXBElement<? extends PointRefStructure>> getPointRef() {
        if (pointRef == null) {
            pointRef = new ArrayList<JAXBElement<? extends PointRefStructure>>();
        }
        return this.pointRef;
    }

}
