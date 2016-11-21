

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class FlexibleLineRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<FlexibleLineRefStructure> flexibleLineRef;

    public List<FlexibleLineRefStructure> getFlexibleLineRef() {
        if (flexibleLineRef == null) {
            flexibleLineRef = new ArrayList<FlexibleLineRefStructure>();
        }
        return this.flexibleLineRef;
    }

}
