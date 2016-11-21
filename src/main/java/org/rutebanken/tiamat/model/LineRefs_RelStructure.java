

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class LineRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends LineRefStructure>> lineRef;

    public List<JAXBElement<? extends LineRefStructure>> getLineRef() {
        if (lineRef == null) {
            lineRef = new ArrayList<JAXBElement<? extends LineRefStructure>>();
        }
        return this.lineRef;
    }

}
