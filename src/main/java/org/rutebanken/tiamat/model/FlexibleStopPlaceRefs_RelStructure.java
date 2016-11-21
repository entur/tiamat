

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class FlexibleStopPlaceRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<FlexibleStopPlaceRefStructure> flexibleStopPlaceRef;

    public List<FlexibleStopPlaceRefStructure> getFlexibleStopPlaceRef() {
        if (flexibleStopPlaceRef == null) {
            flexibleStopPlaceRef = new ArrayList<FlexibleStopPlaceRefStructure>();
        }
        return this.flexibleStopPlaceRef;
    }

}
