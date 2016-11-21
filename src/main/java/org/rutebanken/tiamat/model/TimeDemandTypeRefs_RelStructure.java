

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TimeDemandTypeRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<TimeDemandTypeRefStructure> timeDemandTypeRef;

    public List<TimeDemandTypeRefStructure> getTimeDemandTypeRef() {
        if (timeDemandTypeRef == null) {
            timeDemandTypeRef = new ArrayList<TimeDemandTypeRefStructure>();
        }
        return this.timeDemandTypeRef;
    }

}
