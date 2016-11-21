

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DayTypeRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<DayTypeRefStructure> dayTypeRef;

    public List<DayTypeRefStructure> getDayTypeRef() {
        if (dayTypeRef == null) {
            dayTypeRef = new ArrayList<DayTypeRefStructure>();
        }
        return this.dayTypeRef;
    }

}
