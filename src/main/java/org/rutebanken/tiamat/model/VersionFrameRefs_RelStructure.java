

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class VersionFrameRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends VersionFrameRefStructure>> versionFrameRef;

    public List<JAXBElement<? extends VersionFrameRefStructure>> getVersionFrameRef() {
        if (versionFrameRef == null) {
            versionFrameRef = new ArrayList<JAXBElement<? extends VersionFrameRefStructure>>();
        }
        return this.versionFrameRef;
    }

}
