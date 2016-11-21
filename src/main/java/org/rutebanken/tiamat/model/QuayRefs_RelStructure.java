

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class QuayRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<QuayReference> quayRef;

    public List<QuayReference> getQuayRef() {
        if (quayRef == null) {
            quayRef = new ArrayList<QuayReference>();
        }
        return this.quayRef;
    }

}
