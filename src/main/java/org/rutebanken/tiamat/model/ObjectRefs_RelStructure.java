package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class ObjectRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends VersionOfObjectRefStructure>> versionOfObjectRef;

    public List<JAXBElement<? extends VersionOfObjectRefStructure>> getVersionOfObjectRef() {
        if (versionOfObjectRef == null) {
            versionOfObjectRef = new ArrayList<JAXBElement<? extends VersionOfObjectRefStructure>>();
        }
        return this.versionOfObjectRef;
    }

}
