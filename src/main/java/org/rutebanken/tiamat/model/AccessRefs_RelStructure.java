

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class AccessRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<AccessRefStructure> accessRef;

    public List<AccessRefStructure> getAccessRef() {
        if (accessRef == null) {
            accessRef = new ArrayList<AccessRefStructure>();
        }
        return this.accessRef;
    }

}
