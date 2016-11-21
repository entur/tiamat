

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class ObjectRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends VersionOfObjectRefStructure>> versionOfObjectRef;

    public List<JAXBElement<? extends VersionOfObjectRefStructure>> getVersionOfObjectRef() {
        if (versionOfObjectRef == null) {
            versionOfObjectRef = new ArrayList<JAXBElement<? extends VersionOfObjectRefStructure>>();
        }
        return this.versionOfObjectRef;
    }

}
